/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.prettyfaces.encoding;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.prettyfaces.PrettyFacesITBase;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteITBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class URLEncodingIT extends RewriteITBase
{
   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      return PrettyFacesITBase.getDeployment()
               .addClass(EncodingBean.class)
               .addAsWebResource("encoding/encoding.xhtml", "encoding.xhtml")
               .addAsWebInfResource("encoding/encoding-pretty-config.xml", "pretty-config.xml");
   }

   /**
    * Test a rewrite rule using the 'substitute' attribute to modify the URL.
    * 
    * @see http://code.google.com/p/prettyfaces/issues/detail?id=76
    */
   @Test
   public void testRewriteEncodingSubstitute() throws Exception
   {
      String target = "/virtual/rewrite/substitute";
      String expected = "/virtu%C3%A1ln%C3%AD";

      HttpAction action = get(target);

      String responseContent = action.getResponseContent();
      assertThat(responseContent).contains(action.getContextPath() + expected);
   }

   /**
    * Test a rewrite rule using the 'url' attribute to create a completely new URL.
    * 
    * @see http://code.google.com/p/prettyfaces/issues/detail?id=76
    */
   @Test
   public void testRewriteEncodingUrl() throws Exception
   {
      String target = "/virtual/rewrite/url";
      String expected = "/virtu%C3%A1ln%C3%AD";

      HttpAction action = get(target);

      assertThat(action.getCurrentURL()).endsWith(expected);
      assertThat(action.getResponseContent()).contains(expected);
   }

   @Test
   public void testPrettyFacesFormActionURLEncodesProperly() throws Exception
   {
      String expected = "/custom/form";

      HttpAction action = get(expected);

      assertThat(action.getCurrentURL()).endsWith(expected);
      assertThat(action.getResponseContent()).contains(expected);
   }

   @Test
   // http://code.google.com/p/prettyfaces/issues/detail?id=64
   public void testPrettyFacesFormActionURLEncodesProperlyWithCustomRegexAndMultiplePathSegments() throws Exception
   {
      String expected = "/foo/bar/baz/car/";

      HttpAction action = get(expected);

      assertThat(action.getCurrentURL()).endsWith(expected);
      assertThat(action.getResponseContent()).contains(expected);

      assertThat(action.getResponseContent()).contains("beanPathText=foo/bar/baz/car");
   }

   @Test
   public void testNonMappedRequestRendersRewrittenURL() throws Exception
   {
      HttpAction action = get("/encoding.jsf");

      assertThat(action.getCurrentURL()).endsWith("/encoding.jsf");
      assertThat(action.getResponseContent()).contains("/custom/form");
   }

   @Drone
   WebDriver browser;

   @Test
   public void testURLDecoding() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/encoding/Vračar?dis=Fooo Bar");
      assertThat(browser.getPageSource()).contains("/encoding/Vra%C4%8Dar?dis=Fooo+Bar");
      assertThat(browser.getPageSource()).contains("beanPathText=Vračar");
      assertThat(browser.getPageSource()).contains("beanQueryText=Fooo Bar");
   }

   @Test
   public void testURLDecodingWithPoundSignEncoded() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/encoding/V%23r?dis=gt%23%232206");
      String pageSource = browser.getPageSource();
      assertThat(pageSource).contains("/encoding/V%23r?dis=gt%23%232206");
      assertThat(pageSource).contains("beanPathText=V#r");
      assertThat(pageSource).contains("beanQueryText=gt##2206");
   }

   @Test
   public void testURLDecodingWithPoundSign() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/encoding/V%23r?dis=gt##2206");
      String pageSource = browser.getPageSource();
      assertThat(pageSource).contains("/encoding/V%23r?dis=gt");
      assertThat(pageSource).contains("beanPathText=V#r");
      assertThat(pageSource).contains("beanQueryText=gt");
   }

   @Test
   public void testQueryDecoding() throws Exception
   {
      HttpAction action = get("/encoding/Vračar?dis=Fooo%20Bar");

      assertThat(action.getCurrentURL()).endsWith("/encoding/Vračar?dis=Fooo%20Bar");
      String responseContent = action.getResponseContent();
      assertThat(responseContent).contains("/encoding/Vra%C4%8Dar?dis=Fooo+Bar");
      assertThat(responseContent).contains("beanQueryText=Fooo Bar");
   }

   @Test
   public void testEncodedPathDecoding() throws Exception
   {
      HttpAction action = get("/encoding/Vračar?dis=Fooo%20Bar");

      assertThat(action.getCurrentURL()).endsWith("/encoding/Vračar?dis=Fooo%20Bar");
      assertThat(action.getResponseContent()).contains("/encoding/Vra%C4%8Dar?dis=Fooo+Bar");
      assertThat(action.getResponseContent()).contains("beanPathText=Vračar");
   }

   @Test
   public void testQueryWithGermanUmlaut() throws Exception
   {
      HttpAction action = get("/encoding/Vračar?dis=%C3%BC");
      assertThat(action.getCurrentURL()).endsWith("/encoding/Vračar?dis=%C3%BC");
      assertThat(action.getResponseContent()).contains(getContextPath() + "/encoding/Vra%C4%8Dar?dis=%C3%BC");
      assertThat(action.getResponseContent()).contains("beanPathText=Vračar");
      assertThat(action.getResponseContent()).contains("beanQueryText=\u00fc");
   }

   @Test
   public void testUrlMappingPatternDecoding() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/hard encoding/Vračar");
      assertThat(browser.findElement(By.id("form"))).isNotNull();
   }

   @Test
   public void testEncodedURLMatchesNonEncodedPattern() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/URL%20ENCODED");
      assertThat(browser.findElement(By.id("form"))).isNotNull();
   }

   @Test
   public void testNoDecodeOnSubmitDoesNotCrash() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/decodequery");

      assertThat(browser.getPageSource()).contains("viewId=/encoding.xhtml");
      browser.findElement(By.id("input1")).sendKeys("%");
      browser.findElement(By.id("submit")).click();
      assertThat(browser.getPageSource()).contains("viewId=/encoding.xhtml");
   }

   @Test
   public void testBracesAndBracketsInURL() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/basic/[]{}");
      assertThat(browser.findElement(By.id("form"))).isNotNull();
   }

   @Test
   public void testBracesAndBracketsInURLEncoded() throws Exception
   {
      browser.get(getBaseURL() + getContextPath() + "/basic/%5B%5D%7B%7D");
      assertThat(browser.findElement(By.id("form"))).isNotNull();
   }
}
