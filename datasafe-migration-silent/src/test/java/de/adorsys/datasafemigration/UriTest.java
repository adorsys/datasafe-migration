package de.adorsys.datasafemigration;

import de.adorsys.datasafe_1_0_0.types.api.resource.AbsoluteLocation;
import de.adorsys.datasafe_1_0_0.types.api.resource.BasePrivateResource;
import de.adorsys.datasafe_1_0_0.types.api.resource.PrivateResource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;

@Slf4j
public class UriTest {
    @Test
    @SneakyThrows
    public void test() {

        File tempFile = File.createTempFile("prefix=", "suffix=%");
        URI tempUri = tempFile.toURI();
        String tempfileLocation = tempUri.toASCIIString();
        String tempfileLocationAsString = tempUri.toString();
        String prefix = tempfileLocation.substring(0, tempfileLocation.lastIndexOf("/"));
        String suffix = tempfileLocation.substring(tempfileLocation.lastIndexOf("/"));

        log.info("       uri {}", tempUri);
        log.info("  fullpath {}", tempfileLocation);
        log.info("  tempfile {}", tempfileLocationAsString);
        log.info("    prefix {} suffix {}", prefix, suffix);


// does not work ?
//      java.lang.IllegalArgumentException: Resource location must be absolute BasePrivateResource{container=Uri{uri=QwRzMp31yiBek9CdICwCHZcwRBQv4g_Eq_VVE1xAXEQ=/yGhy3JfdNaq7vYRRMKX81iYtgKXA2jThHmQR79HoN-k=/Y7hTMGFBDBm2qbfHv4GpNfGEUWToYEQMoRlwAuPIHLk=/imPJ4aIgO4m6ngZ5mI8oQlSMWJx4daxy3LViNvxTyAM=/iyalS38UJIhDH7XMCQUuLpHO0w3anZ-CBhsWGO8TAcs=/5jK3CVsL8ywmD6TFOen9e4UtDeRU6b4m8k0Nb5HQadM=/5UN3b0iD9nEbtnj13t7ykIzfeqiYvS-7WBc0JdG_HuE=}, encryptedPath=Uri{uri=}, decryptedPath=Uri{uri=}}

//        String sourcePath = prefix + suffix;
//        AbsoluteLocation<PrivateResource> sourceLocation = BasePrivateResource.forAbsolutePrivate(sourcePath);
//        log.info("uri {}", sourceLocation.location().asURI().toASCIIString());

    }
}
