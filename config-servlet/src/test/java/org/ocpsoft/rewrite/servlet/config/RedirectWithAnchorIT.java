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
package org.ocpsoft.rewrite.servlet.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.RewriteIT;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * @see https://github.com/ocpsoft/rewrite/issues/81
 * @author Christian Kaltepoth
 */
@RunWith(Arquillian.class)
public class RedirectWithAnchorIT extends RewriteIT
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteIT.getDeployment()
               .addClass(RedirectWithAnchorProvider.class)
               .addAsServiceProvider(ConfigurationProvider.class, RedirectWithAnchorProvider.class);
   }

   @ArquillianResource
   private java.net.URL baseUrl;

   @Test
   public void testRedirectToUrlWithAnchor() throws Exception
   {
      WebDriver driver = new HtmlUnitDriver();
      driver.get(baseUrl.toString() + "do");
      assertThat(driver.getCurrentUrl()).endsWith("/it#now");
   }
}