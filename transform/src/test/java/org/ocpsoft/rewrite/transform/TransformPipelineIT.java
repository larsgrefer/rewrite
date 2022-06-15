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
package org.ocpsoft.rewrite.transform;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteIT;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 
 * Integration test for core {@link Transposition} API.
 * 
 * @author Christian Kaltepoth
 * 
 */
@RunWith(Arquillian.class)
public class TransformPipelineIT extends RewriteIT
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive archive = RewriteIT.getDeployment()
               .addAsWebResource(new StringAsset("this is foo"), "test.txt")
               .addClasses(TransformPipelineTestProvider.class, FooBarTransformer.class, UppercaseTransformer.class)
               .addAsServiceProvider(ConfigurationProvider.class, TransformPipelineTestProvider.class);
      return archive;
   }

   @Test
   public void testPipelineWithOneTransformer() throws Exception
   {
      HttpAction action = get("/test.one");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).isEqualTo("this is bar");
   }

   @Test
   public void testPipelineWithTwoTransformers() throws Exception
   {
      HttpAction action = get("/test.two");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).isEqualTo("THIS IS BAR");
   }

}
