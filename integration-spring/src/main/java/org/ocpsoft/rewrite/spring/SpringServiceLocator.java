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
package org.ocpsoft.rewrite.spring;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.ocpsoft.common.spi.ServiceLocator;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * {@link ServiceLocator} implementation for Spring.
 * 
 * @author Christian Kaltepoth
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class SpringServiceLocator implements ServiceLocator
{
   @Override
   @SuppressWarnings("unchecked")
   public <T> Collection<Class<T>> locate(Class<T> clazz)
   {
      Set<Class<T>> result = new LinkedHashSet<Class<T>>();

      ServletContext servletContext = SpringServletContextLoader.getCurrentServletContext();
      WebApplicationContext applicationContext = null;
      if (servletContext != null) {
         applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
      }
      else {
         applicationContext = ContextLoader.getCurrentWebApplicationContext();
      }

      // may be null if Spring hasn't started yet
      if (applicationContext != null) {

         // ask spring about SPI implementations
         Map<String, T> beans = applicationContext.getBeansOfType(clazz);

         // add the implementations Class objects to the result set
         for (T type : beans.values()) {
            result.add((Class<T>) type.getClass());
         }

      }

      return result;
   }

}
