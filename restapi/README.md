# Rest API node

Simple headless node with a Rest API to provide access to KOINZ network data as well as KOINZ DAO data.
It is used for KOINZ 2 to request data about the DAO state as well as account age and account witness data for reputation use cases.


To run 'RestApiMain' you need to have Bitcoin node running and have 'blocknotify' in the `bitcoin.conf` set up.


### Run Rest API node

Run the Gradle task:

```sh
./gradlew restapi:run
```

Or create a run scrip by:

```sh
./gradlew restapi:startKOINZApp
```

And then run:

```sh
./koinz-restapi
```

### Customize with program arguments

Example program arguments for running at localhost with  Regtest:
```sh
./koinz-restapi \
    --baseCurrencyNetwork=BTC_REGTEST \
    --useDevPrivilegeKeys=true \
    --useLocalhostForP2P=true \
    --nodePort=3333 \
    --appName=koinz-BTC_REGTEST_restapi \
    --fullDaoNode=true \
    --rpcUser=[RPC USER] \
    --rpcPassword=[RPC PW] \
    --rpcPort=18443 \
    --rpcBlockNotificationPort=5123
```



