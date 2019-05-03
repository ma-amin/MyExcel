package ir.chamran.myexcel.adapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import ir.chamran.myexcel.model.ShopDetails;

public class ShopDetailsDiffCallback  extends DiffUtil.Callback {

    private final List<ShopDetails> mOldEmployeeList;
    private final List<ShopDetails> mNewEmployeeList;

    ShopDetailsDiffCallback(List<ShopDetails> oldEmployeeList, List<ShopDetails> newEmployeeList) {
        this.mOldEmployeeList = oldEmployeeList;
        this.mNewEmployeeList = newEmployeeList;
    }

    @Override
    public int getOldListSize() {
        return mOldEmployeeList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewEmployeeList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldEmployeeList.get(oldItemPosition).getDbID() == mNewEmployeeList.get(newItemPosition).getDbID();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final ShopDetails oldEmployee = mOldEmployeeList.get(oldItemPosition);
        final ShopDetails newEmployee = mNewEmployeeList.get(newItemPosition);

        return oldEmployee.equals(newEmployee);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
