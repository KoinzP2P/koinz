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

package koinz.common.config;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;

import lombok.Getter;

public enum BaseCurrencyNetwork {
    KOINZ_MAINNET(MainNetParams.get(), "BTC", "MAINNET", "Bitcoin"),
    KOINZ_TESTNET(TestNet3Params.get(), "BTC", "TESTNET", "Bitcoin"),
    KOINZ_REGTEST(RegTestParams.get(), "BTC", "REGTEST", "Bitcoin"),
    KOINZ_DAO_TESTNET(RegTestParams.get(), "BTC", "REGTEST", "Bitcoin"), // server side regtest until v0.9.5
    KOINZ_DAO_BETANET(MainNetParams.get(), "BTC", "MAINNET", "Bitcoin"), // mainnet test genesis
    KOINZ_DAO_REGTEST(RegTestParams.get(), "BTC", "REGTEST", "Bitcoin"); // server side regtest after v0.9.5, had breaking code changes so we started over again

    @Getter
    private final NetworkParameters parameters;
    @Getter
    private final String currencyCode;
    @Getter
    private final String network;
    @Getter
    private final String currencyName;

    BaseCurrencyNetwork(NetworkParameters parameters, String currencyCode, String network, String currencyName) {
        this.parameters = parameters;
        this.currencyCode = currencyCode;
        this.network = network;
        this.currencyName = currencyName;
    }

    public boolean isMainnet() {
        return "KOINZ_MAINNET".equals(name());
    }

    public boolean isTestnet() {
        return "KOINZ_TESTNET".equals(name());
    }

    public boolean isDaoTestNet() {
        return "KOINZ_DAO_TESTNET".equals(name());
    }

    public boolean isDaoRegTest() {
        return "KOINZ_DAO_REGTEST".equals(name());
    }

    public boolean isDaoBetaNet() {
        return "KOINZ_DAO_BETANET".equals(name());
    }

    public boolean isRegtest() {
        return "KOINZ_REGTEST".equals(name());
    }

    public long getDefaultMinFeePerVbyte() {
        return 15;  // 2021-02-22 due to mempool congestion, increased from 2
    }
}
