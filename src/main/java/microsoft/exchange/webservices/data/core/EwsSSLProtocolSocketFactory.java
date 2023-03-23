/*
 * The MIT License
 * Copyright (c) 2012 Microsoft Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package microsoft.exchange.webservices.data.core;

import java.security.GeneralSecurityException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.conn.ssl.X509HostnameVerifier;

/**
 * <p>
 * EwsSSLProtocolSocketFactory can be used to create SSL {@link java.net.Socket}s
 * that accept self-signed certificates.
 * </p>
 * <p>
 * This socket factory SHOULD NOT be used for productive systems
 * due to security reasons, unless it is a conscious decision and
 * you are perfectly aware of security implications of accepting
 * self-signed certificates
 * </p>
 * Example of using custom protocol socket factory for a specific host:
 * <pre>
 *     Protocol easyhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
 *
 *     URI uri = new URI("https://localhost/", true);
 *     // use relative url only
 *     GetMethod httpget = new GetMethod(uri.getPathQuery());
 *     HostConfiguration hc = new HostConfiguration();
 *     hc.setHost(uri.getHost(), uri.getPort(), easyhttps);
 *     HttpClient client = new HttpClient();
 *     client.executeMethod(hc, httpget);
 *     </pre>
 * </p>
 * <p>
 * Example of using custom protocol socket factory per default instead of the standard one:
 * <pre>
 *     Protocol easyhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
 *     Protocol.registerProtocol("https", easyhttps);
 *
 *     HttpClient client = new HttpClient();
 *     GetMethod httpget = new GetMethod("https://localhost/");
 *     client.executeMethod(httpget);
 *     </pre>
 * </p>
 *
 * <p>
 * DISCLAIMER: HttpClient developers DO NOT actively support this component.
 * The component is provided as a reference material, which may be inappropriate
 * for use without additional customization.
 * </p>
 */

public class EwsSSLProtocolSocketFactory extends SSLConnectionSocketFactory {

  /**
   * Default hostname verifier.
   */
	private static final X509HostnameVerifier DEFAULT_HOSTNAME_VERIFIER = new StrictHostnameVerifier();


  /**
   * The SSL Context.
   */
  private final SSLContext sslcontext;


  /**
   * Constructor for EasySSLProtocolSocketFactory.
   *
   * @param context          SSL context
   * @param hostnameVerifier hostname verifier
   */
  public EwsSSLProtocolSocketFactory(
			final SSLContext context, final X509HostnameVerifier hostnameVerifier
  ) {
    super(context, hostnameVerifier);
    this.sslcontext = context;
  }


  /**
   * Create and configure SSL protocol socket factory using default hostname verifier.
   * {@link EwsSSLProtocolSocketFactory#DEFAULT_HOSTNAME_VERIFIER}
   *
   * @param trustManager trust manager
   * @return socket factory for SSL protocol
   * @throws GeneralSecurityException on security error
   */
  public static EwsSSLProtocolSocketFactory build(final TrustManager trustManager)
    throws GeneralSecurityException {
    return build(trustManager, DEFAULT_HOSTNAME_VERIFIER);
  }

  /**
   * Create and configure SSL protocol socket factory using trust manager and hostname verifier.
   *
   * @param trustManager trust manager
   * @param hostnameVerifier hostname verifier
   * @return socket factory for SSL protocol
   * @throws GeneralSecurityException on security error
   */
  public static EwsSSLProtocolSocketFactory build(
			final TrustManager trustManager, final X509HostnameVerifier hostnameVerifier
  ) throws GeneralSecurityException {
    final SSLContext sslContext = createSslContext(trustManager);
    return new EwsSSLProtocolSocketFactory(sslContext, hostnameVerifier);
  }

  /**
   * Create SSL context and initialize it using specific trust manager.
   *
   * @param trustManager trust manager
   * @return initialized SSL context
   * @throws GeneralSecurityException on security error
   */
  public static SSLContext createSslContext(final TrustManager trustManager)
    throws GeneralSecurityException {
    final EwsX509TrustManager x509TrustManager = new EwsX509TrustManager(null, trustManager);
    final SSLContext sslContext = SSLContexts.createDefault();
    sslContext.init(
      null,
      new TrustManager[] { x509TrustManager },
      null
    );
    return sslContext;
  }


  /**
   * @return SSL context
   */
  public SSLContext getContext() {
    return sslcontext;
  }

  @Override
public boolean equals(final Object obj) {
    return ((obj != null) && obj.getClass().equals(EwsSSLProtocolSocketFactory.class));
  }

  @Override
public int hashCode() {
    return EwsSSLProtocolSocketFactory.class.hashCode();
  }

}
