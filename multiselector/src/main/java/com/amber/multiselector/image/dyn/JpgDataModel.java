package com.amber.multiselector.image.dyn;

/**
 * Created by luosiyi on 2017/11/1.
 */

public class JpgDataModel implements IDataModel {
    private String dataModelUrl;

    public JpgDataModel(String dataModelUrl) {
        this.dataModelUrl = dataModelUrl;
    }

    @Override
    public String buildDataModelUrl(int width, int height) {
        //http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200/format/jpg
        return String.format("%s?imageView2/1/w/%d/h/%d/format/jpg", dataModelUrl, width, height);
    }
}
