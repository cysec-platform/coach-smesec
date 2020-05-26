package eu.smesec.library;

import eu.smesec.bridge.Library;
import eu.smesec.bridge.generated.Metadata;
import eu.smesec.bridge.generated.Mvalue;
import eu.smesec.bridge.md.MetadataUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MetadataBuilder implements IMetadataBuilder {
    private String name;
    private String id;
    private String description;
    private String link;
    private String order;
    private String general;
    private String image;
    private String clazz;
    private String urlPrefix;
    private String linkPrefix = "app/coach.jsp#";
    private List<Mvalue> mvalues = new ArrayList();
    private Library library;

    private MetadataBuilder(Library lib) {
        library = lib;
        urlPrefix = library.getQuestionnaire().getId() + "/" + library.getId() + "/";
    }

    public static MetadataBuilder newInstance(Library lib) {
        return new MetadataBuilder(lib);
    }

    public MetadataBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public MetadataBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public MetadataBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public MetadataBuilder setLink(String link) {
        this.link = link;
        return this;
    }

    public MetadataBuilder setLinkOnCoach(String coach, String question) {
        this.link = linkPrefix + coach + "," + question;
        return this;
    }

    public MetadataBuilder setOrder(String order) {
        this.order = order;
        return this;
    }

    public MetadataBuilder setGeneral(String general) {
        this.general = general;
        return this;
    }

    public MetadataBuilder setImage(String image) {
        this.image = urlPrefix + image;
        return this;
    }

    public MetadataBuilder setClazz(String clazz) {
        this.clazz = clazz;
        return this;
    }

    public MetadataBuilder setMvalue(String key, String value){
        mvalues.add(MetadataUtils.createMvalueStr(key, value));
        return this;
    }

    public Metadata buildCustom(String mdKey) {
        return MetadataUtils.createMetadata(mdKey, mvalues);

    }

    @Override
    public Metadata buildBadge() {
        return MetadataUtils.createMetadata(MetadataUtils.MD_BADGES + "." + id,
                Arrays.asList(
                        MetadataUtils.createMvalueStr(MetadataUtils.MV_NAME, name),
                        MetadataUtils.createMvalueStr(MetadataUtils.MV_ID, id),
                        MetadataUtils.createMvalueStr(MetadataUtils.MV_CLASS, clazz),
                        MetadataUtils.createMvalueStr(MetadataUtils.MV_IMAGE, image),
                        MetadataUtils.createMvalueStr(MetadataUtils.MV_LINK, link),
                        MetadataUtils.createMvalueStr(MetadataUtils.MV_DESCRIPTION, description))
                );
    }

    @Override
    public Metadata buildRecommendation() {
        return MetadataUtils.createMetadata(MetadataUtils.MD_RECOMMENDED + "." + id,
            Arrays.asList(
                MetadataUtils.createMvalueStr(MetadataUtils.MV_NAME, name),
                MetadataUtils.createMvalueStr(MetadataUtils.MV_ID, id),
                MetadataUtils.createMvalueStr(MetadataUtils.MV_DESCRIPTION, description),
                MetadataUtils.createMvalueStr(MetadataUtils.MV_ORDER, order),
                MetadataUtils.createMvalueStr(MetadataUtils.MV_GENERAL, general),
                MetadataUtils.createMvalueStr(MetadataUtils.MV_LINK, link),
                MetadataUtils.createMvalueStr(MetadataUtils.MV_IMAGE, image)
            ));
    }
}
