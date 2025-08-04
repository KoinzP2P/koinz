/*
 * This file is part of KOINZ.
 *
 * KOINZ is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * KOINZ is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with KOINZ. If not, see <http://www.gnu.org/licenses/>.
 */

package koinz.core.account.witness;

import koinz.core.account.sign.SignedWitness;
import koinz.core.account.sign.SignedWitnessService;
import koinz.core.payment.PaymentAccount;
import koinz.core.payment.payload.PaymentAccountPayload;
import koinz.core.trade.model.bisq_v1.Trade;
import koinz.core.util.JsonUtil;

import koinz.network.p2p.storage.P2PDataStorage;

import koinz.common.app.DevEnv;
import koinz.common.crypto.CryptoException;
import koinz.common.crypto.Hash;
import koinz.common.crypto.KeyRing;
import koinz.common.crypto.PubKeyRing;
import koinz.common.crypto.Sig;
import koinz.common.util.Hex;
import koinz.common.util.Utilities;

import java.security.KeyPair;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public class AccountAgeWitnessUtils {
    private final AccountAgeWitnessService accountAgeWitnessService;
    private final SignedWitnessService signedWitnessService;
    private final KeyRing keyRing;

    AccountAgeWitnessUtils(AccountAgeWitnessService accountAgeWitnessService,
                           SignedWitnessService signedWitnessService,
                           KeyRing keyRing) {
        this.accountAgeWitnessService = accountAgeWitnessService;
        this.signedWitnessService = signedWitnessService;
        this.keyRing = keyRing;
    }

    // Log tree of signed witnesses
    public void logSignedWitnesses() {
        var orphanSigners = signedWitnessService.getRootSignedWitnessSet(true);
        log.info("Orphaned signed account age witnesses:");
        orphanSigners.forEach(w -> {
            log.info("{}: Signer PKH: {} Owner PKH: {} time: {}", w.getVerificationMethod().toString(),
                    Utilities.bytesAsHexString(Hash.getRipemd160hash(w.getSignerPubKey())).substring(0, 7),
                    Utilities.bytesAsHexString(Hash.getRipemd160hash(w.getWitnessOwnerPubKey())).substring(0, 7),
                    w.getDate());
            logChild(w, "  ", new Stack<>());
        });
    }

    private void logChild(SignedWitness sigWit, String initString, Stack<P2PDataStorage.ByteArray> excluded) {
        log.info("{}AEW: {} PKH: {} time: {}", initString,
                Utilities.bytesAsHexString(sigWit.getAccountAgeWitnessHash()).substring(0, 7),
                Utilities.bytesAsHexString(Hash.getRipemd160hash(sigWit.getWitnessOwnerPubKey())).substring(0, 7),
                sigWit.getDate());
        signedWitnessService.getSignedWitnessMapValues().forEach(w -> {
            if (!excluded.contains(new P2PDataStorage.ByteArray(w.getWitnessOwnerPubKey())) &&
                    Arrays.equals(w.getSignerPubKey(), sigWit.getWitnessOwnerPubKey())) {
                excluded.push(new P2PDataStorage.ByteArray(w.getWitnessOwnerPubKey()));
                logChild(w, initString + "  ", excluded);
                excluded.pop();
            }
        });
    }

    // Log signers per
    public void logSigners() {
        log.info("Signers per AEW");
        Collection<SignedWitness> signedWitnessMapValues = signedWitnessService.getSignedWitnessMapValues();
        signedWitnessMapValues.forEach(w -> {
                    log.info("AEW {}", Utilities.bytesAsHexString(w.getAccountAgeWitnessHash()));
                    signedWitnessMapValues.forEach(ww -> {
                        if (Arrays.equals(w.getSignerPubKey(), ww.getWitnessOwnerPubKey())) {
                            log.info("  {}", Utilities.bytesAsHexString(ww.getAccountAgeWitnessHash()));
                        }
                    });
                }
        );
    }

    public void logUnsignedSignerPubKeys() {
        log.info("Unsigned signer pubkeys");
        signedWitnessService.getUnsignedSignerPubKeys().forEach(signedWitness ->
                log.info("PK hash {} date {}",
                        Utilities.bytesAsHexString(Hash.getRipemd160hash(signedWitness.getSignerPubKey())),
                        signedWitness.getDate()));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Debug logs
    ///////////////////////////////////////////////////////////////////////////////////////////

    private String getWitnessDebugLog(PaymentAccountPayload paymentAccountPayload,
                                      PubKeyRing pubKeyRing) {
        Optional<AccountAgeWitness> accountAgeWitness =
                accountAgeWitnessService.findWitness(paymentAccountPayload, pubKeyRing);
        if (!accountAgeWitness.isPresent()) {
            byte[] accountInputDataWithSalt =
                    accountAgeWitnessService.getAccountInputDataWithSalt(paymentAccountPayload);
            byte[] hash = Hash.getSha256Ripemd160hash(Utilities.concatenateByteArrays(accountInputDataWithSalt,
                    pubKeyRing.getSignaturePubKeyBytes()));
            return "No accountAgeWitness found for paymentAccountPayload with hash " + Utilities.bytesAsHexString(hash);
        }

        AccountAgeWitnessService.SignState signState =
                accountAgeWitnessService.getSignState(accountAgeWitness.get());
        return signState.name() + " " + signState.getDisplayString() +
                "\n" + accountAgeWitness.toString();
    }

    public void witnessDebugLog(Trade trade, @Nullable AccountAgeWitness myWitness) {
        // Log to find why accounts sometimes don't get signed as expected
        // TODO: Demote to debug or remove once account signing is working ok
        checkNotNull(trade.getContract());
        checkNotNull(trade.getContract().getBuyerPaymentAccountPayload());
        boolean checkingSignTrade = true;
        boolean isBuyer = trade.getContract().isMyRoleBuyer(keyRing.getPubKeyRing());
        AccountAgeWitness witness = myWitness;
        if (witness == null) {
            witness = isBuyer ?
                    accountAgeWitnessService.getMyWitness(trade.getContract().getBuyerPaymentAccountPayload()) :
                    accountAgeWitnessService.getMyWitness(trade.getContract().getSellerPaymentAccountPayload());
            checkingSignTrade = false;
        }
        boolean isSignWitnessTrade = accountAgeWitnessService.accountIsSigner(witness) &&
                !accountAgeWitnessService.peerHasSignedWitness(trade) &&
                accountAgeWitnessService.tradeAmountIsSufficient(trade.getAmount());
        log.info("AccountSigning debug log: " +
                        "\ntradeId: {}" +
                        "\nis buyer: {}" +
                        "\nbuyer account age witness info: {}" +
                        "\nseller account age witness info: {}" +
                        "\nchecking for sign trade: {}" +
                        "\nis myWitness signer: {}" +
                        "\npeer has signed witness: {}" +
                        "\ntrade amount: {}" +
                        "\ntrade amount is sufficient: {}" +
                        "\nisSignWitnessTrade: {}",
                trade.getId(),
                isBuyer,
                getWitnessDebugLog(trade.getContract().getBuyerPaymentAccountPayload(),
                        trade.getContract().getBuyerPubKeyRing()),
                getWitnessDebugLog(trade.getContract().getSellerPaymentAccountPayload(),
                        trade.getContract().getSellerPubKeyRing()),
                checkingSignTrade, // Following cases added to use same logic as in seller signing check
                accountAgeWitnessService.accountIsSigner(witness),
                accountAgeWitnessService.peerHasSignedWitness(trade),
                trade.getAmount(),
                accountAgeWitnessService.tradeAmountIsSufficient(trade.getAmount()),
                isSignWitnessTrade);
    }

    static class AccountAgeWitnessDto {
        private final String profileId;
        private final String hashAsHex;
        private final long date;
        private final String pubKeyBase64;
        private final String signatureBase64;

        public AccountAgeWitnessDto(String profileId,
                                    String hashAsHex,
                                    long date,
                                    String pubKeyBase64,
                                    String signatureBase64) {
            this.profileId = profileId;
            this.hashAsHex = hashAsHex;
            this.date = date;
            this.pubKeyBase64 = pubKeyBase64;
            this.signatureBase64 = signatureBase64;
        }
    }

    static class SignedWitnessDto {
        private final String profileId;
        private final String hashAsHex;
        private final long accountAgeWitnessDate;
        private final long witnessSignDate;
        private final String pubKeyBase64;
        private final String signatureBase64;

        public SignedWitnessDto(String profileId,
                                String hashAsHex,
                                long accountAgeWitnessDate,
                                long witnessSignDate,
                                String pubKeyBase64,
                                String signatureBase64) {
            this.profileId = profileId;
            this.hashAsHex = hashAsHex;
            this.accountAgeWitnessDate = accountAgeWitnessDate;
            this.witnessSignDate = witnessSignDate;
            this.pubKeyBase64 = pubKeyBase64;
            this.signatureBase64 = signatureBase64;
        }
    }

    public static Optional<String> signAccountAgeAndBisq2ProfileId(AccountAgeWitnessService accountAgeWitnessService,
                                                                   PaymentAccount account,
                                                                   KeyRing keyRing,
                                                                   String profileId) {
        return accountAgeWitnessService.findWitness(account.getPaymentAccountPayload(), keyRing.getPubKeyRing())
                .map(accountAgeWitness -> {
                    try {
                        checkArgument(!accountAgeWitnessService.isFilteredWitness(accountAgeWitness), "Invalid account age witness");
                        String hashAsHex = Hex.encode(accountAgeWitness.getHash());
                        long date = accountAgeWitness.getDate();
                        checkArgument(date > 0, "Date must be > 0");
                        String message = profileId + hashAsHex + date;
                        KeyPair signatureKeyPair = keyRing.getSignatureKeyPair();
                        String signatureBase64 = Sig.sign(signatureKeyPair.getPrivate(), message);
                        String pubKeyBase64 = Base64.getEncoder().encodeToString(Sig.getPublicKeyBytes(signatureKeyPair.getPublic()));
                        AccountAgeWitnessDto dto = new AccountAgeWitnessDto(profileId,
                                hashAsHex,
                                date,
                                pubKeyBase64,
                                signatureBase64);
                        return JsonUtil.objectToJson(dto);
                    } catch (CryptoException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public static Optional<String> signSignedWitnessAndBisq2ProfileId(AccountAgeWitnessService accountAgeWitnessService,
                                                                      PaymentAccount account,
                                                                      KeyRing keyRing,
                                                                      String profileId) {
        return accountAgeWitnessService.findWitness(account.getPaymentAccountPayload(), keyRing.getPubKeyRing())
                .map(accountAgeWitness -> {
                    try {
                        checkArgument(!accountAgeWitnessService.isFilteredWitness(accountAgeWitness), "Invalid account age witness");
                        long witnessSignDate = accountAgeWitnessService.getWitnessSignDate(accountAgeWitness);
                        long ageInDays = (System.currentTimeMillis() - witnessSignDate) / TimeUnit.DAYS.toMillis(1);
                        if (!DevEnv.isDevMode()) {
                            checkArgument(witnessSignDate > 0, "Account is not signed yet");
                            checkArgument(ageInDays > 60, "Account must have been signed at least 61 days ago");
                        }

                        String hashAsHex = Hex.encode(accountAgeWitness.getHash());
                        long date = accountAgeWitness.getDate();
                        checkArgument(date > 0, "AccountAgeWitness date must be > 0");
                        String message = profileId + hashAsHex + date + witnessSignDate;
                        KeyPair signatureKeyPair = keyRing.getSignatureKeyPair();
                        String signatureBase64 = Sig.sign(signatureKeyPair.getPrivate(), message);
                        String pubKeyBase64 = Base64.getEncoder().encodeToString(Sig.getPublicKeyBytes(signatureKeyPair.getPublic()));
                        SignedWitnessDto dto = new SignedWitnessDto(profileId,
                                hashAsHex,
                                date,
                                witnessSignDate,
                                pubKeyBase64,
                                signatureBase64);
                        return JsonUtil.objectToJson(dto);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
