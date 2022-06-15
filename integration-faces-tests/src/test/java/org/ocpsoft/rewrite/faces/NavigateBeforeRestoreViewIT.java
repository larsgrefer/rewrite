package org.ocpsoft.rewrite.faces;

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

@RunWith(Arquillian.class)
public class NavigateBeforeRestoreViewIT extends RewriteITBase
{

  @Deployment(testable = false)
  public static WebArchive getDeployment()
  {
     return RewriteIT.getDeployment()
              .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteAnnotationArchive())
              .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteFacesArchive())
              .addClass(NavigateBeforeRestoreViewBean.class)
              .addAsWebResource("navigate-before-restoreview.xhtml");
  }

  @Test
  public void testNavigateBeforeRestoreView() throws Exception
  {
    HttpAction action = get("/navigate");
    assertThat(200).isEqualTo(action.getStatusCode());
  }
}
