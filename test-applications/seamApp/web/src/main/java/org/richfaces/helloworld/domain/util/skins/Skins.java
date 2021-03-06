package org.richfaces.helloworld.domain.util.skins;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectOne;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * @author Nick Belaevski - nbelaevski@exadel.com created 09.02.2007
 * 
 */
@Name("skinBean")
@Scope(ScopeType.SESSION)
public class Skins {

	private String[] skinsArray = new String[] { "blueSky", "classic",
			"deepMarine", "DEFAULT", "emeraldTown", "japanCherry", "ruby",
			"wine", "plain" };

	private String defaultSkin = "deepMarine";

	private String skin = defaultSkin;

	private UISelectOne createComponent() {
		UISelectOne selectOne = new HtmlSelectOneRadio();
		selectOne.setValue(skin);

		for (int i = 0; i < skinsArray.length; i++) {
			String skinName = skinsArray[i];

			UISelectItem item = new UISelectItem();
			item.setItemLabel(skinName);
			item.setItemValue(skinName);
			item.setId("skinSelectionFor_" + skinName);

			selectOne.getChildren().add(item);
		}

		return selectOne;
	}

	public String getSkin() {
		return skin;
	}

	public UIComponent getSkinComponent() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map requestMap = facesContext.getExternalContext().getRequestMap();
		Object object = requestMap.get("SkinBean");
		if (object != null) {
			return (UISelectOne) object;
		}

		UISelectOne selectOne = createComponent();
		requestMap.put("SkinBean", selectOne);
		return selectOne;
	}

	public void setSkinComponent(UIComponent component) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map requestMap = facesContext.getExternalContext().getRequestMap();
		requestMap.put("SkinBean", component);
	}

	public String change() {
		UISelectOne selectOne = (UISelectOne) getSkinComponent();
		skin = (String) selectOne.getValue();
		return null;
	}
}
