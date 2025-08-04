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

package koinz.core.locale;

import koinz.asset.AddressValidationResult;
import koinz.asset.BitcoinAddressValidator;
import koinz.asset.Coin;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;

public class MockTestnetCoin extends Coin {

    public MockTestnetCoin(Network network, NetworkParameters networkParameters) {
        super("MockTestnetCoin", "MOCK_COIN", new BSQAddressValidator(networkParameters), network);
    }

    public static class Mainnet extends MockTestnetCoin {

        public Mainnet() {
            super(Network.MAINNET, MainNetParams.get());
        }
    }

    public static class Testnet extends MockTestnetCoin {

        public Testnet() {
            super(Network.TESTNET, TestNet3Params.get());
        }
    }

    public static class Regtest extends MockTestnetCoin {

        public Regtest() {
            super(Network.REGTEST, RegTestParams.get());
        }
    }

    public static class BSQAddressValidator extends BitcoinAddressValidator {

        public BSQAddressValidator(NetworkParameters networkParameters) {
            super(networkParameters);
        }

        @Override
        public AddressValidationResult validate(String address) {
            return super.validate(address);
        }
    }
}
