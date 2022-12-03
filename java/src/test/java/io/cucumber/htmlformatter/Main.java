package io.cucumber.htmlformatter;

import io.cucumber.messages.NdjsonToMessageIterable;
import io.cucumber.messages.NdjsonToMessageIterable.Deserializer;
import io.cucumber.messages.types.Envelope;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static io.cucumber.htmlformatter.Jackson.OBJECT_MAPPER;

public final class Main {
    private static final Deserializer deserializer = (json) -> OBJECT_MAPPER.readValue(json, Envelope.class);

    public static void main(String[] args) throws IOException {
        InputStream in = System.in;
        if (args.length == 1) {
            in = new FileInputStream(args[0]);
        }
        try (NdjsonToMessageIterable envelopes = new NdjsonToMessageIterable(in, deserializer)) {
            try (MessagesToJunitXmlWriter htmlWriter = new MessagesToJunitXmlWriter(System.out)) {
                for (Envelope envelope : envelopes) {
                    htmlWriter.write(envelope);
                }
            }
        } catch (Throwable e) {
            // Workaround for https://github.com/mojohaus/exec-maven-plugin/issues/141
            e.printStackTrace();
            System.exit(1);
        }
    }
}
