

# About #
Článek popisuje možnosti přidání nebo potlačení položek menu v systému K4.

## Menu ##
Menu v systému K4 je možno rozdělit do dvou oblastí:  `Hlavní` a `kontextové`.

### Hlavní menu ###
Hlavní menu je "schované" pod tlačítkem `Více`, je dostupné odkudkoliv a obsahuje
akce, které nejsou závislé na aktuálním výběru.

![http://kramerius.googlecode.com/svn/wiki/features_46/main-menu.png](http://kramerius.googlecode.com/svn/wiki/features_46/main-menu.png)

### Kontextové menu ###
Kontextové menu se zobrazí pouze v případech, kdy máme vybraný jeden nebo více titulů, tj. při zobrazení konkrétního titulu nebo při hledávání.
Jednotlivé akce kontextového menu s aktuálním výběrem nějak operují.

![http://kramerius.googlecode.com/svn/wiki/features_46/context-menu.png](http://kramerius.googlecode.com/svn/wiki/features_46/context-menu.png)

Hlavní a kontextové menu jsou rozděleny na dvě části, administrátorskou a veřejnou. To, jestli se zobrazí administrační část daného menu je řízeno
akcí  `display_admin_menu` viz [Prava#Akce](Prava#Akce.md).

# Programová realizace menu #
Každá položka menu je realizovaná vlastní třídou, která se stará o její vyrendrování v podobě html snippetu.
Všechny položky musí implementovat rozhraní [MenuItem](http://kramerius.googlecode.com/svn/trunk/search/src/java/cz/incad/Kramerius/exts/menu/MenuItem.java) z balíčku `cz.incad.Kramerius.exts.menu`.

Pro položky z kontextového menu existuje značkovací rozrhaní [ContextMenuItem](http://kramerius.googlecode.com/svn/trunk/search/src/java/cz/incad/Kramerius/exts/menu/context/ContextMenuItem.java)
resp. [AdminContextMenuItem](http://kramerius.googlecode.com/svn/trunk/search/src/java/cz/incad/Kramerius/exts/menu/context/impl/adm/AdminContextMenuItem.java) pro položku z administrátorské části menu a [PublicContextMenuItem](http://kramerius.googlecode.com/svn/trunk/search/src/java/cz/incad/Kramerius/exts/menu/context/impl/pub/PublicContextMenuItem.java)
pro položky veřejné.

Podobné je tomu i u hlavního menu. Zde je společné rozrhaní [MainMenuItem](http://kramerius.googlecode.com/svn/trunk/search/src/java/cz/incad/Kramerius/exts/menu/main/MainMenuItem.java), administrační položky jsou reprezentovány
[AdminMenuItem](http://kramerius.googlecode.com/svn/trunk/search/src/java/cz/incad/Kramerius/exts/menu/main/impl/adm/AdminMenuItem.java) a [PublicMainMenuItem](http://kramerius.googlecode.com/svn/trunk/search/src/java/cz/incad/Kramerius/exts/menu/main/impl/pub/PublicMainMenuItem.java) pro položky veřejné.

Všechny položky jsou spojovány konfiguračního modulem pomocí techlonogie guice, konkrétně metodou, která se v terminilogii guice nazývá multibinding.
Vše nejlépe demonstruje následující kusy kódu.

Definice položky
```
/**
 * Reprezentuje polozku menu pro spusteni dialogu procesu
 */        
public class ProcessesDialog extends AbstractMainMenuItem implements AdminMenuItem {

    @Override
    public boolean isRenderable() {
        return hasUserAllowedAction(SecuredActions.MANAGE_LR_PROCESS.getFormalName());
    }

    @Override
    public String getRenderedItem() throws IOException {
        return renderMainMenuItem(
            "javascript:processes.processes(); javascript:hideAdminMenu();",
            "administrator.menu.dialogs.lrprocesses.title", false);
    }
}
```

Zapojení položky do modulu

```
        ...
        Multibinder<AdminMenuItem> adminItems
        = Multibinder.newSetBinder(binder(), AdminMenuItem.class);
        adminItems.addBinding().to(ProcessesDialog.class);
        ...
```



# Přidání vlastní položky #
Přidání vlastní položky spočívá pouze v implementaci správného rozhraní a vytvoření jednoho guice modulu a definici manifestu pro načtení modulu.

Bude doplněno.


