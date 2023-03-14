package org.radarbase.oauth.unit;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.radarbase.exception.TokenException;
import org.radarbase.oauth.OAuth2AccessTokenDetails;
import org.radarbase.oauth.OAuth2Client;

import jakarta.ws.rs.core.HttpHeaders;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by dverbeec on 31/08/2017.
 */
class OAuth2ClientTest {
    private static final String accessToken =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyYWRhcl9yZXN0YXBp"
            + "Iiwi19NYW5hZ2VtZW50UG9ydGFsIl0sInNvdXJjZXMiOltdLCJzY29wZSI6WyJyZWFkIl0sImlzcyI6Ik1hb"
            + "mFnZW1lbnleHAiOjE1MDQwODU3MzEsImlhdCI6MTUwNDA4MzkzMSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0"
            + "VSIl0sImp0aSI6TJmMTItNDQxMi1iZGVjLTc5YzMxNWY3NGM3OSIsImNsaWVudF9pZCI6InJhZGFyX3Jlc3R"
            + "hcGkifQ.J0TEFQAUnH9RFaplURHrbeLelgbAr3CS7os_Y5S6836TFZyDe4mz4LqhxJLquXxTNP3DYddOKDD_"
            + "RQ1t0nIDfx0hFJawPB3AjVqobRLOtFQWWdtYYmPbDXVQkdK41iVDhl_15BBxxOlT0pFQfkq4wk22ubq5cg8V"
            + "Z57xDkrfgaIbdowntnK9GqLy6mDtaPdQV23VDr3whkjEq2YJ9AQBj4KiOWEVAYuNwhZFwHwInsYPZTs2RNK5"
            + "WkdW2pe4sXGc7BDgUykpUWEMtL7BoyTZEGO5VqDkwcbio1zJDGB5dPm8VHWtlg4tH098BhsFrVE3zOJ9D0Ai"
            + "62JWZkzr24lH9QjBwruxifyu4AvcLp_AxmO7m_r1bLcDuh6Yt4Ntm1bhGoB_PrygiOFPMn2-VnUH9zTxpZaK"
            + "UH9CHHKOVdcK9N3gLKo30ETVDib-bZS-rDESHDvnYppgTH6i31wfjl80NCQhSpB3GyXAR2YHfoTj4VbEzGKs"
            + "LEfS7g-4hSH2kY4-srOAH5TeI2snKbh76mFL8SOTuZrHf-F5KwWPqB82OzAr899eFk6uiNd5Uz7dICyEKyS7"
            + "v-HQ";
    private static final String accessTokenId = "5b9fc645-2f12-4412-bdec-79c315f74c79";
    private long tokenIssueDate;

    private static final String invalidScopeResponse = "{\n"
            + "  \"error\" : \"invalid_scope\",\n"
            + "  \"error_description\" : \"Invalid scope: write\",\n"
            + "  \"scope\" : \"read\"\n"
            + "}\n";

    private static final String invalidCredentialsResponse = "{\n"
            + "  \"timestamp\" : \"2017-08-31T09:50:19.779+0000\",\n"
            + "  \"status\" : 401,\n"
            + "  \"error\" : \"Unauthorized\",\n"
            + "  \"message\" : \"Bad credentials\",\n"
            + "  \"path\" : \"/oauth/token\""
            + "}";

    private static final String invalidGrantTypeResponse = "{\n"
            + "  \"error\" : \"invalid_client\",\n"
            + "  \"error_description\" : \"Unauthorized grant type: client_credentials\"\n"
            + "}";

    private static final String notFoundResponse = "{\n"
            + "  \"timestamp\" : \"2017-08-31T12:00:56.274+0000\",\n"
            + "  \"status\" : 404,\n"
            + "  \"error\" : \"Not Found\",\n"
            + "  \"message\" : \"Not Found\",\n"
            + "  \"path\" : \"/oauth/token\"\n"
            + "}\n";

    private static OkHttpClient httpClient;
    private static WireMockServer wireMockServer;
    private OAuth2Client.Builder clientBuilder;

    /** Set up custom HTTP client. */
    @BeforeAll
    public static void setUpClass() {
        wireMockServer = new WireMockServer(new WireMockConfiguration().port(8089));
        wireMockServer.start();
        httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    }

    @BeforeEach
    public void init() throws MalformedURLException {
        tokenIssueDate = Instant.now().getEpochSecond();
        clientBuilder = new OAuth2Client.Builder()
            .credentials("client", "secret")
            .endpoint(new URL("http://localhost:8089/oauth/token"));
    }

    @AfterEach
    public void reset() {
        wireMockServer.resetAll();
    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testValidTokenResponse() throws TokenException {
        wireMockServer.stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .withBody(successfulResponse())));
        OAuth2Client client = clientBuilder
                .scopes("read")
                .httpClient(httpClient)
                .build();
        OAuth2AccessTokenDetails token = client.getValidToken();
        assertTrue(token.isValid());
        assertFalse(token.isExpired());
        assertEquals(accessToken, token.getAccessToken());
        assertEquals("bearer", token.getTokenType());
        assertEquals(1799L, token.getExpiresIn());
        assertTrue(client.isTokenValidFor(Duration.ofSeconds(1700)));
        assertFalse(client.isTokenValidFor(Duration.ofSeconds(1900)));
        assertEquals(tokenIssueDate, token.getIssueDate());
        assertEquals("radar_restapi", token.getSubject());
        assertEquals("read", token.getScope());
        assertEquals(accessTokenId, token.getJsonWebTokenId());
        assertEquals("ManagementPortal", token.getIssuer());
    }

    @Test
    void testInvalidScope() {
        wireMockServer.stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(invalidScopeResponse)));
        OAuth2Client client = clientBuilder
                .scopes("write")
                .build();
        assertThrows(TokenException.class, () -> client.getValidToken());
    }

    @Test
    void testInvalidCredentials() {
        wireMockServer.stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(invalidCredentialsResponse)));
        OAuth2Client client = clientBuilder
                .scopes("read")
                .build();
        assertThrows(TokenException.class, () -> client.getValidToken());
    }

    @Test
    void testInvalidGrantType() {
        wireMockServer.stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(invalidGrantTypeResponse)));
        OAuth2Client client = clientBuilder
                .scopes("read")
                .build();
        assertThrows(TokenException.class, () -> client.getValidToken(Duration.ofSeconds(30)));
    }

    @Test
    void testInvalidMapping() {
        wireMockServer.stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(invalidTypesResponse())));
        OAuth2Client client = clientBuilder
                .scopes("read")
                .build();
        assertThrows(TokenException.class, () -> client.getValidToken(Duration.ofSeconds(30)));
    }

    @Test
    void testUnreachableServer() throws MalformedURLException {
        // no http stub here so the location will be unreachable
        OAuth2Client client = clientBuilder
                // different port in case wiremock is not cleaned up yet
                .endpoint(new URL("http://localhost:9000"))
                .scopes("read")
                .build();
        assertThrows(TokenException.class, () -> client.getValidToken());
    }

    @Test
    void testParseError() {
        wireMockServer.stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/html")
                        .withBody("<html>Oops, no JSON here</html>")));
        OAuth2Client client = clientBuilder
                .scopes("read")
                .build();
        assertThrows(TokenException.class, () -> client.getValidToken());
    }

    @Test
    void testNotFound() {
        wireMockServer.stubFor(post(urlEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(notFoundResponse)));
        OAuth2Client client = clientBuilder
                .scopes("read")
                .build();
        assertThrows(TokenException.class, () -> client.getValidToken());
    }

    private String successfulResponse() {
        return "{\n"
                + "  \"access_token\" : \"" + accessToken + "\",\n"
                + "  \"token_type\" : \"bearer\",\n"
                + "  \"expires_in\" : 1799,\n"
                + "  \"scope\" : \"read\",\n"
                + "  \"sub\" : \"radar_restapi\",\n"
                + "  \"sources\" : [ ],\n"
                + "  \"iss\" : \"ManagementPortal\",\n"
                + "  \"iat\" : " + tokenIssueDate + ",\n"
                + "  \"jti\" : \"" + accessTokenId + "\"\n"
                + "}";
    }

    private String invalidTypesResponse() {
        return "{\n"
                + "  \"access_token\" : \"" + accessToken + "\",\n"
                + "  \"token_type\" : \"bearer\",\n"
                + "  \"expires_in\" : \"tomorrow\",\n"
                + "  \"scope\" : \"read\",\n"
                + "  \"sub\" : \"radar_restapi\",\n"
                + "  \"sources\" : [ ],\n"
                + "  \"iss\" : \"ManagementPortal\",\n"
                + "  \"iat\" : " + tokenIssueDate + ",\n"
                + "  \"jti\" : \"" + accessTokenId + "\"\n"
                + "}";
    }
}
