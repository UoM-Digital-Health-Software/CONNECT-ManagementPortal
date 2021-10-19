package org.radarbase.management;

import io.github.jhipster.config.JHipsterConstants;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.annotation.PostConstruct;
import org.radarbase.management.config.ApplicationProperties;
import org.radarbase.management.config.DefaultProfileUtil;
import org.radarbase.management.config.ManagementPortalProperties;
import org.radarbase.management.config.SourceTypeLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

/**
 * This is the application configuration that excludes CommandLineRunner(i.e the sourceTypeLoader).
 * This is used for testing to replicate the application setup without SourceTypeLoader.
 */
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        value = SourceTypeLoader.class))
@EnableZuulProxy
@EnableAutoConfiguration
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class,
        ManagementPortalProperties.class})
public class ManagementPortalTestApp {

    private static final Logger log = LoggerFactory.getLogger(ManagementPortalTestApp.class);

    private final Environment env;

    public ManagementPortalTestApp(Environment env) {
        this.env = env;
    }

    /**
     * Initializes ManagementPortal.
     *
     * <p>Spring profiles can be configured with a program arguments
     * --spring.profiles.active=your-active-profile</p>
     *
     * <p>You can find more information on how profiles work with JHipster on
     * <a href="http://jhipster.github.io/profiles/">
     *     http://jhipster.github.io/profiles/
     * </a></p>.
     */
    @PostConstruct
    public void initApplication() {
        if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT))
                && env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_PRODUCTION))) {
            log.error("You have misconfigured your application! It should not run "
                    + "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT))
                && env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_CLOUD))) {
            log.error("You have misconfigured your application! It should not"
                    + "run with both the 'dev' and 'cloud' profiles at the same time.");
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments
     * @throws UnknownHostException if the local host name could not be resolved into an address
     */
    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(ManagementPortalTestApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        log.info("\n--------------------------------------------------\n\t"
                        + "Application '{}' is running! Access URLs:\n\t"
                        + "Local: \t\t{}://localhost:{}\n\t"
                        + "External: \t{}://{}:{}\n\t"
                        + "Profile(s): \t{}\n--------------------------------------------------",
                env.getProperty("spring.application.name"),
                protocol,
                env.getProperty("server.port"),
                protocol,
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                env.getActiveProfiles());
    }
}
