FROM azul/zulu-openjdk:17.0.8-17.46.19 as build

COPY '{{ ARCHIVE_PATH }}' /
RUN tar xvf /koinz-seednode.tar

FROM gcr.io/distroless/java17-debian11
COPY --from=build /koinz-seednode /koinz-seednode

LABEL org.opencontainers.image.title="KOINZ Seednode"
LABEL org.opencontainers.image.description="KOINZ P2P Network Seed Node"
LABEL org.opencontainers.image.version="1.0.0"
LABEL org.opencontainers.image.vendor="KOINZ P2P"
LABEL org.opencontainers.image.licenses="AGPL-3.0"

ENTRYPOINT ["java", "-classpath", "'{{ CLASS_PATH }}'", "'{{ MAIN_CLASS }}'"]
