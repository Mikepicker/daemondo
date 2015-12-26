package com.daemondo.mike.daemondo;

/**
 * Created by mike on 23/12/15.
 */
public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
