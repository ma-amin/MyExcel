package ir.chamran.myexcel.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ir.chamran.myexcel.R;
import ir.chamran.myexcel.databinding.ItemMainBinding;
import ir.chamran.myexcel.model.ShopDetails;
import ir.chamran.myexcel.utilities.Utils;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private Activity activity;
    @Nullable
    private OnPostClickCallback callback;
    private List<ShopDetails> posts;
    private List<ShopDetails> mSearchPostList;
    private ValueFilter valueFilter;

    public PostsAdapter(Activity activity, @Nullable OnPostClickCallback callback) {
    /*public PostsAdapter(Activity activity, List<ShopDetails> posts) {
        this.posts = posts;*/
        this.activity = activity;
        this.callback = callback;

        //setHasStableIds(true);
    }

    public interface OnPostClickCallback {
        void onClick(ShopDetails shopDetails);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemMainBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_main, parent, false);
        binding.setCallback(callback);
        return new PostsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PostsViewHolder postsViewHolder = (PostsViewHolder) holder;
        postsViewHolder.binding.setShopDetails(posts.get(position));
        /*if (posts.get(position).isState())
            postsViewHolder.binding.tvStateIconMain.setText(R.string.true_circle_icon);
        else
            postsViewHolder.binding.tvStateIconMain.setText(R.string.false_circle_icon);
        holder.itemView.setOnClickListener((View.OnClickListener) holder);*/

        postsViewHolder.binding.executePendingBindings();
    }

    public List<ShopDetails> getData() {
        return posts;
    }

    public void updateList(List<ShopDetails> newPosts) {
        if (posts == null) {
            posts = newPosts;
            mSearchPostList = newPosts;
            notifyItemRangeInserted(0, newPosts.size());

        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new ShopDetailsDiffCallback(posts, newPosts));
            posts = newPosts;
            mSearchPostList = newPosts;
            result.dispatchUpdatesTo(this);
        }
    }

    public void removeItem(int position) {
        posts.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(ShopDetails item, int position) {
        posts.add(position, item);
        notifyItemInserted(position);
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String strSearch = constraint.toString();
            if (strSearch.isEmpty()) {
                posts = mSearchPostList;

            } else {
                List<ShopDetails> filterList = new ArrayList<>();

                for (ShopDetails searchList : mSearchPostList) {
                    if (searchList.getType().contains(strSearch) || searchList.getFullName().contains(strSearch) ||
                            searchList.getRegion().contains(strSearch) || searchList.getDistrict().contains(strSearch)) {
                        filterList.add(searchList);
                    }
                }
                posts = filterList;
            }

            FilterResults results = new FilterResults();
            results.count = posts.size();
            results.values = posts;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //posts = (ArrayList<ShopDetails>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return posts == null ? 0 : posts.size();
    }

    private class PostsViewHolder extends RecyclerView.ViewHolder {
    //public class PostsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemMainBinding binding;

        private PostsViewHolder(final ItemMainBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;

            Utils.setFontIcon(activity, binding.tvIconMain);
            Utils.setFontIcon(activity, binding.tvTimeIconMain);
            Utils.setFontIcon(activity, binding.tvUserIconMain);
            Utils.setFontIcon(activity, binding.tvCityIconMain);
            Utils.setFontIcon(activity, binding.tvStateIconMain);
        }

        /*@Override
        public void onClick(View v) {
            /*Intent intent = new Intent(activity, PostDetailsActivity.class);
            intent.putExtra(PostDetailsActivity.KEY_POST_ID, postID);
            activity.startActivity(intent);
        }*/
    }
}
