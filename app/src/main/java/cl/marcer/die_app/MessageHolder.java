package cl.marcer.die_app;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Alberto Galleguillos on 4/3/17.
 */

public class MessageHolder extends RecyclerView.ViewHolder {
    public final ImageView mImage;
    private final TextView mAuthor;
    private final TextView mTitle;
    private final TextView mBody;
    private final TextView mUrl;

    public MessageHolder(View itemView) {
        super(itemView);
        mImage = (ImageView) itemView.findViewById(R.id.iv_author);
        mAuthor = (TextView) itemView.findViewById(R.id.tv_author);
        mTitle = (TextView) itemView.findViewById(R.id.tv_title);
        mBody = (TextView) itemView.findViewById(R.id.tv_body);
        mUrl = (TextView) itemView.findViewById(R.id.tv_url);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v,getAdapterPosition());
            }
        });
    }

    private MessageHolder.ClickListener mClickListener;

    public interface ClickListener{
        public void onItemClick(View view,int position);
    }

    public void setOnClickListener(MessageHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }

    public void setmAuthor(String author){
        mAuthor.setText(author);
    }
    public void setmTitle(String title){
        mTitle.setText(title);
    }
    public void setmBody(String body){
        mBody.setText(body);
    }
    public void setmUrl(String url){ mUrl.setText(url); }
}
