/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.gwt.config;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.gwt.server.history.HistoryRewriteConfiguration;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteIT;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class HistoryRewriteRootContextConfigurationIT extends RewriteIT
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteIT
               .getDeployment("ROOT.war")
               // The TomEE Arquillian adapter doesn't like ROOT.war, so we force the root context path via context.xml
               .addAsManifestResource(new StringAsset("<Context path=\"\"/>"), "context.xml")
               // required for Glassfish to get application to the root context path
               .addAsWebInfResource(new StringAsset("<glassfish-web-app><context-root>/</context-root></glassfish-web-app>"),
                        "glassfish-web.xml")
               .addAsWebResource(new StringAsset(""), "index.html")
               .addAsServiceProvider(ConfigurationProvider.class, HistoryRewriteConfiguration.class);
      return deployment;
   }

   @Test
   public void testContextPathServedFromHeadRequest()
   {
      HttpAction action = head("/?org.ocpsoft.rewrite.gwt.history.contextPath");
      assertThat(action.getStatusCode()).isEqualTo(200);

      assertThat(action.getResponseHeaderValues("org.ocpsoft.rewrite.gwt.history.contextPath").get(0)).isEqualTo("/");
   }

   @Test
   public void testContextPathNotServedFromGetRequest() throws Exception
   {
      HttpAction action = get("/index.html?org.ocpsoft.rewrite.gwt.history.contextPath");
      assertThat(action.getStatusCode()).isEqualTo(200);

      assertThat(action.getResponseHeaderValues("org.ocpsoft.rewrite.gwt.history.contextPath").isEmpty()).isTrue();
   }

   @Test
   public void testContextPathServedFromCookieOnNormalRequest() throws Exception
   {
      HttpAction action = get("/index.html");
      assertThat(action.getStatusCode()).isEqualTo(200);

      String cookie = action.getResponseHeaderValues("Set-Cookie").get(0);
      assertThat(cookie.contains("org.ocpsoft.rewrite.gwt.history.contextPath=" + action.getContextPath())).isTrue();
   }

}