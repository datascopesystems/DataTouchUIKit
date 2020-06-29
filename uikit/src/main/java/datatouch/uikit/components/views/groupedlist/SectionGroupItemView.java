package datatouch.uikit.components.views.groupedlist;

import datatouch.uikit.components.views.groupedlist.viewmodels.SectionGroupItemViewModel;

public interface SectionGroupItemView {

    void setSelectedState();

    void setNormalState();

    int getViewModelId();

    void setOnItemClickListener(OnSectionItemClickCallback onItemClickCallback);

    interface OnSectionItemClickCallback {
        void onItemClick(SectionGroupItemViewModel viewModel);
    }

}