## Bitcoin node monitor

This is a simple headless node with a http server which connects periodically to the KOINZ-provided Bitcoin nodes and
disconnect quickly afterwards.

### Run Bitcoin node monitor

Run the Gradle task:

```sh
./gradlew btcnodemonitor:run
```

Or create a run scrip by:

```sh
./gradlew btcnodemonitor:startKOINZApp
```

And then run:

```sh
./koinz-btcnodemonitor
```

