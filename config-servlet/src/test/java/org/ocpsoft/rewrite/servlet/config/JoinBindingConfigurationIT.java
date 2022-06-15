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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteIT;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class JoinBindingConfigurationIT extends RewriteIT
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteIT
               .getDeployment()
               .addPackages(true, ConfigRoot.class.getPackage())
               .addAsServiceProvider(ConfigurationProvider.class, JoinBindingConfigurationProvider.class);
      return deployment;
   }

   @Test
   public void testUrlMappingConfiguration() throws Exception
   {
      HttpAction action = get("/bind/23");
      assertThat(action.getStatusCode()).isEqualTo(201);
   }

   @Test
   public void testUrlMappingConfigurationWithoutInboundCorrection() throws Exception
   {
      HttpAction action = get("/bind.html");
      assertThat(action.getStatusCode()).isEqualTo(404);
   }
   
   @Test
   public void testUrlMappingWithoutRepeatedParameters() throws Exception
   {
      HttpAction action = get("/users/didiez");
      String[] expected = new String[]{"didiez"};
      
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseHeaderValues("userId").toArray()).isEqualTo(expected);
   }
}