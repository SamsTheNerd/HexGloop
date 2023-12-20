package com.samsthenerd.hexgloop.misc.clientgreatbook;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.mixins.booktweaks.MixinClientBookPageAccessor;
import com.samsthenerd.hexgloop.mixins.booktweaks.MixinClientGetPageTemplate;
import com.samsthenerd.hexgloop.mixins.booktweaks.MixinClientGetTemplateComponents;
import com.samsthenerd.hexgloop.mixins.booktweaks.MixinClientGetTemplateText;
import com.samsthenerd.hexgloop.mixins.booktweaks.MixinClientYoinkPPLangKey;
import com.samsthenerd.hexgloop.utils.patternmatching.PatternMatching;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.template.TemplateComponent;
import vazkii.patchouli.client.book.text.BookTextParser;
import vazkii.patchouli.client.book.text.Span;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

@Environment(EnvType.CLIENT)
public class PatternPageLookup {
    // cache them up here
    private static final Map<Identifier, BookPage> foundPages = new HashMap<>();

    public static BookPage findPage(Identifier id) {
        if(foundPages.containsKey(id)) {
            return foundPages.get(id);
        }
        // fine if it's null
        BookPage foundPage = searchForPage(id);
        foundPages.put(id, foundPage);
        return foundPage;
    }

    public static BookPage findPage(HexPattern pattern){
        Identifier id = PatternMatching.getIdentifier(pattern);
        // HexGloop.logPrint("got id: " + id);
        if(id != null){
            BookPage foundPage = findPage(id);
            // HexGloop.logPrint("got page: " + foundPage);
            return foundPage;
        }
        return null;
    }

    private static Text patchiFormat(Text text){
        Text baseText = text;
        if(text.getContent() instanceof LiteralTextContent lc) {
			baseText = Text.literal(I18n.translate(lc.string()));
		}
        Book hexbook = BookRegistry.INSTANCE.books.get(HexAPI.modLoc("thehexbook"));
        GuiBook gui = hexbook.getContents().getCurrentGui();
        BookTextParser parser = new BookTextParser(gui, hexbook, 0, 0, GuiBook.PAGE_WIDTH, GuiBook.TEXT_LINE_HEIGHT, Style.EMPTY);
        MutableText styledText = Text.empty();
        for(Span span : parser.parse(baseText)){
            styledText.append(Text.literal(span.text).setStyle(span.style));
        }
        return styledText;
    }

    public static Pair<Text, Text> getDescription(Identifier id) {
        BookPage page = findPage(id);
        if(page instanceof MixinClientGetPageTemplate pageTemplate && pageTemplate.getTemplate() instanceof MixinClientGetTemplateComponents bookTemplate) {
            int foundTexts = 0;
            Text argText = Text.empty();
            for(TemplateComponent component : bookTemplate.getComponents()){
                if(component instanceof MixinClientGetTemplateText textComponent){
                    if(foundTexts == 0){
                        // it's the input/output, which could still be useful !
                        argText = patchiFormat(textComponent.getActualText());
                    } else {
                        return new Pair<>(argText, patchiFormat(textComponent.getActualText()));
                    }
                    foundTexts++;
                }
            }
        }
        return new Pair<>(Text.literal("No description found for " + id.toString()), Text.empty());
    }

    public static Pair<Text, Text> getDescription(HexPattern pattern){
        Identifier id = PatternMatching.getIdentifier(pattern);
        if(id != null){
            return getDescription(id);
        }
        return new Pair<>(Text.empty(), Text.empty());
    }

    @Nullable
    private static BookPage searchForPage(Identifier id){
        // HexGloop.logPrint("need to search for page for " + id.toString());
        Book hexbook = BookRegistry.INSTANCE.books.get(HexAPI.modLoc("thehexbook"));
        for(BookEntry entry : hexbook.getContents().entries.values()){
            // HexGloop.logPrint("checking entry: " + entry.getName());
            for(BookPage page : entry.getPages()){
                String anchor = ((MixinClientBookPageAccessor)(Object)page).getAnchor();
                if(id.toString().equals(anchor)){
                    return page;
                }
                if(page instanceof MixinClientGetPageTemplate pageTemplate 
                && pageTemplate.getTemplate() instanceof MixinClientGetTemplateComponents bookTemplate 
                && bookTemplate.getProcessor() instanceof MixinClientYoinkPPLangKey patternProcessor) {
                    String transKey = patternProcessor.getTranslationKey();
                    String opId = transKey.replace("hexcasting.spell.book.", "").replace("hexcasting.spell.", "");
                    if(id.toString().equals(opId)){
                        return page;
                    }
                }
            }
        }
        return null;
    }
}
