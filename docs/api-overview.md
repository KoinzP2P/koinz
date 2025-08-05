## API overview

Starting with v1.9.0 you can use pre-built versions of the KOINZ cli (koinz-cli-v1.9.11.zip) and KOINZ daemon (
koinz-daemon-v1.9.11.zip) to use KOINZ without touching the user interface.

Just download the archives and extract them locally. You have to run the daemon to access the local KOINZ daemon API
endpoints.

To run daemon.jar on Mainnet:

`$ java -jar daemon.jar --apiPassword=becareful`

If you just want to control your headless daemon within your terminal you have to run the KOINZ cli as well.
Again just download the koinz-cli archive and extract it locally.
To call getversion from cli.jar

`$ java -jar cli.jar --password=becareful getversion`

You can use the KOINZ API to access local KOINZ daemon API endpoints, which provide a subset of the KOINZ Desktop
application's feature set: check balances, transfer BTC and KNZ, create payment accounts, view offers, create and take
offers, and execute trades.

The KOINZ API is based on the gRPC framework, and any supported gRPC language binding can be used to call KOINZ API
endpoints.

You'll find in-depth documentation and examples under following link: https://koinz-network.github.io/slate/#introduction

KOINZ gRPC API reference documentation example source code is hosted on GitHub
at https://github.com/koinz-network/koinz-api-reference. Java and Python developers interested in bot development may find
this Intellij project useful for running the existing examples, and writing their own bots.

For additional developer support please join Development - KOINZ v1 on Matrix.
