package datatouch.uikit.components.listitems;
public interface SelectableView {
    boolean isCurrentlySelected();
    boolean toggleSelection();
    void setSelection(boolean selection);
}
