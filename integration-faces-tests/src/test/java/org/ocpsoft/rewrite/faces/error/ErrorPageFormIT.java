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
package org.ocpsoft.rewrite.faces.error;

import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.faces.annotation.RewriteFacesAnnotationsTest;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteIT;
import org.ocpsoft.rewrite.test.RewriteITBase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Arquillian.class)
public class ErrorPageFormIT extends RewriteITBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteIT.getDeployment()
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteFacesArchive())
               .addClass(ErrorBean.class)
               .addAsWebInfResource("error-web.xml", "web.xml")
               .addAsWebResource("error-page.xhtml", "error.xhtml");
   }

   @Test
   public void testNavigateWithSimpleString() throws Exception
   {
      HttpAction client = get("/missingresource");
      assertThat(client.getCurrentURL()).endsWith("/missingresource");
      assertThat(client.getStatusCode()).isEqualTo(404);

      String content = client.getResponseContent();
      Assertions.assertThat(content)
               .matches("(?s).*action=\"" + client.getContextPath() + "/faces/error\\.xhtml.*");
   }

}
