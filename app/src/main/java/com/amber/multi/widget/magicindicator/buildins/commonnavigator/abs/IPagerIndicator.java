package com.amber.multi.widget.magicindicator.buildins.commonnavigator.abs;






import com.amber.multi.widget.magicindicator.buildins.commonnavigator.model.PositionData;

import java.util.List;

/**
 * 抽象的viewpager指示器，适用于CommonNavigator
 * Created by luosiyi on 2017/7/18.
 */
public interface IPagerIndicator {
    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void onPageSelected(int position);

    void onPageScrollStateChanged(int state);

    void onPositionDataProvide(List<PositionData> dataList);
}
