package com.example.managehouse.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managehouse.Activity.HomeActivity;
import com.example.managehouse.Common.Common;
import com.example.managehouse.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public abstract class MySwipeHelper extends ItemTouchHelper.SimpleCallback {

    public static int buttonWidth;
    private RecyclerView recyclerView;
    private List<ButtonThaoTac> buttonList = null;
    private GestureDetector gestureDetector = null;
    private int swipePosition = -1;
    private float swipeThreshold = 0.5f;
    private Map<Integer, List<ButtonThaoTac>> buttonBuffer = null;
    private Queue<Integer> removerQueue = null;
    public boolean itemSwipe = true;

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            for (ButtonThaoTac buttonThaoTac : buttonList) {
                if (buttonThaoTac.onClick(e.getX(), e.getY())) break;
            }
            return true;
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            try {
                if (swipePosition < 0) return false;
                Point point = new Point((int) event.getRawX(), (int) event.getRawY());
                RecyclerView.ViewHolder swipeViewHolder = recyclerView.findViewHolderForAdapterPosition(swipePosition);
                View swipedItem = swipeViewHolder.itemView;
                Rect rect = new Rect();
                swipedItem.getGlobalVisibleRect(rect);

                if (event.getAction() == MotionEvent.ACTION_DOWN ||
                        event.getAction() == MotionEvent.ACTION_UP ||
                        event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (rect.top < point.y && rect.bottom > point.y) {
                        gestureDetector.onTouchEvent(event);
                    } else {
                        removerQueue.add(swipePosition);
                        swipePosition = -1;
                        recoverSwipedItem();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

    };

    private synchronized void recoverSwipedItem() {
        while (!removerQueue.isEmpty()) {
            int pos = removerQueue.poll();
            if (pos > -1) {
                recyclerView.getAdapter().notifyItemChanged(pos);
            }
        }
    }

    public MySwipeHelper(Context context, RecyclerView recyclerView) {
        super(0, ItemTouchHelper.LEFT);
        this.recyclerView = recyclerView;
        this.buttonList = new ArrayList<>();
        this.gestureDetector = new GestureDetector(context, gestureListener);
        this.recyclerView.setOnTouchListener(onTouchListener);
        this.buttonBuffer = new HashMap<>();

        removerQueue = new LinkedList<Integer>() {
            @Override
            public boolean add(Integer integer) {

                if (contains(integer)) {
                    return false;
                } else return super.add(integer);
            }
        };
        attachSwipe();
    }

    private void attachSwipe() {

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return itemSwipe;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int pos = viewHolder.getAdapterPosition();
        if (swipePosition != pos) {
            removerQueue.add(swipePosition);
        }
        swipePosition = pos;
        if (buttonBuffer.containsKey(swipePosition)) {
            buttonList = buttonBuffer.get(swipePosition);
        } else {
            buttonList.clear();
        }
        buttonBuffer.clear();
        swipeThreshold = 0.5f * buttonList.size() * buttonWidth;
        recoverSwipedItem();
    }

    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return swipeThreshold;

    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 0.1f * defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return 5.0f * defaultValue;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        int pos = viewHolder.getAdapterPosition();
        float translationX = dX;
        View itemView = viewHolder.itemView;
        if (pos < 0) {
            swipePosition = pos;
            return;
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                List<ButtonThaoTac> buffer = new ArrayList<>();
                if (!buttonBuffer.containsKey(pos)) {
                    instantiateButtonThaoTac(viewHolder, buffer);
                    buttonBuffer.put(pos, buffer);
                } else {
                    buffer = buttonBuffer.get(pos);

                }
                translationX = dX * buffer.size() * buttonWidth / itemView.getWidth();
                drawButton(c, itemView, buffer, pos, translationX);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
    }

    private void drawButton(Canvas c, View itemView, List<ButtonThaoTac> buffer, int pos, float translationX) {
        float right = itemView.getRight();
        float dButtonWidth = -1 * translationX / buffer.size();
        for (ButtonThaoTac buttonThaoTac : buffer) {
            float left = right - dButtonWidth;
            buttonThaoTac.onDraw(c, new RectF(left, itemView.getTop(), right, itemView.getBottom()), pos);
            right = left;
        }
    }

    public abstract void instantiateButtonThaoTac(RecyclerView.ViewHolder viewHolder, List<ButtonThaoTac> buffer);

    public class ButtonThaoTac {
        private String text;
        private int imageId, textSize, color, pos;
        private RectF clickRegion;
        private ButtonThaoTacClickListener listener;
        private Context context;
        private Resources resources;

        public ButtonThaoTac(Context context, String text, int imageId, int textSize, int color, ButtonThaoTacClickListener listener) {
            this.text = text;
            this.imageId = imageId;
            this.textSize = textSize;
            this.color = color;
            this.listener = listener;
            this.context = context;
            resources = context.getResources();
        }

        public boolean onClick(float x, float y) {
            if (clickRegion != null && clickRegion.contains(x, y)) {
                listener.onClick(pos);
                return true;
            }
            return false;
        }

        public void onDraw(Canvas canvas, RectF rectF, int pos) {
            int color = this.color;
            String text = this.text;
            String text2 = "";
            boolean check = false;
            if(text.equals("Tình trạng")) {
                check = true;
                if(Common.hoadonList.size() > 0){
                    if(Common.hoadonList.get(pos).getStatus() == 1) {
                        color = Color.parseColor("#27ae60");
                        text = "Đã";
                        text2 = "thu tiền";
                    }
                    else {
                        if(Common.hoadonList.get(pos).getStatus() == 2) {
                            color = Color.parseColor("#f39c12");
                            text = "Chờ";
                            text2 = "thu tiền";
                        }
                        else {
                            color = Color.parseColor("#f39c12");
                            text = "Bỏ hủy";
                        }
                    }
                }
            }

            Paint paint = new Paint();
            paint.setColor(color);
            canvas.drawRect(rectF, paint);
            //text
            paint.setColor(Color.WHITE);
            paint.setTextSize(textSize);

            Rect rect = new Rect();
            float cHeight = rectF.height();
            float cWidth = rectF.width();
            paint.setTextAlign(Paint.Align.LEFT);
            paint.getTextBounds(text, 0, text.length(), rect);
            float x = 0, y = 0;
            if (imageId == 0) {
                x = cWidth / 2f - rect.width() / 2f - rect.left;
                y = cHeight / 2f + rect.height() / 2f - rect.bottom;
                if(check && !text2.equals("")) {
                    canvas.drawText(text, rectF.left + x, rectF.top + y - 20, paint);
                    canvas.drawText(text2, rectF.left + x / 5, rectF.top + y + 40, paint);
                }
                else {
                    canvas.drawText(text, rectF.left + x, rectF.top + y, paint);
                }

            } else {
                Drawable drawable = ContextCompat.getDrawable(context, imageId);
                Bitmap bitmap = drawableToBitmap(drawable);
                canvas.drawBitmap(bitmap, ((rectF.left + rectF.right) / 2) , ((rectF.top + rectF.bottom) / 2), paint);
            }
            clickRegion = rectF;
            this.pos = pos;
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) return ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
