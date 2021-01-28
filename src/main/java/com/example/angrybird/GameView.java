package com.example.angrybird;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


public class GameView extends View {

    private Handler handler;
    private Runnable runnable;
    private int UPDATE_MILLIS=10,dWidth,dHeight,birdIndex=0;
    private Bitmap background;
    private Display display;
    private Point point;
    private Rect rect1,rect2,birdRect;
    private Bitmap[] birds;
    private Bitmap[] tree;
    private Rect[] treeRect;
    private boolean loop=true;
    private int gravity=15,velocity=3,birdX=0,birdY=0,treeCount=0;
    private int rect1X=0,rect2X=0,rect1Back=0,rect2Back=0,rectWidth=0,rect1Width=0,rect2Width=0,supp1X=0;
    private Random random;
    private int[] treeX,treeY,treeHeight;
    private Paint paint;
    private Context context;


    public GameView(Context context) {
        super(context);
        this.context=context;

        random=new Random();
        treeRect=new Rect[5];
        treeX=new int[5];
        treeY=new int[5];
        treeHeight=new int[5];

        initComponent();

        asignTreeX();

        handler=new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
    }

    private void initComponent(){
        background= BitmapFactory.decodeResource(getResources(),R.drawable.background2);
        display=((Activity)getContext()).getWindowManager().getDefaultDisplay();
        point=new Point();
         display.getSize(point);
        dWidth=point.x;
        dHeight=point.y;
        birdY=dHeight/2;
        rect1X=0;
        birds=new Bitmap[5];
        tree=new Bitmap[5];
        birds[0]=BitmapFactory.decodeResource(getResources(),R.drawable.bird0);
        birds[1]=BitmapFactory.decodeResource(getResources(),R.drawable.bird1);
        birds[2]=BitmapFactory.decodeResource(getResources(),R.drawable.bird2);
        birds[3]=BitmapFactory.decodeResource(getResources(),R.drawable.bird3);
        birds[4]=BitmapFactory.decodeResource(getResources(),R.drawable.bird4);
        tree[0]=BitmapFactory.decodeResource(getResources(),R.drawable.tree1);
        tree[1]=BitmapFactory.decodeResource(getResources(),R.drawable.tree2);
        tree[2]=BitmapFactory.decodeResource(getResources(),R.drawable.tree3);
        tree[3]=BitmapFactory.decodeResource(getResources(),R.drawable.tree4);
        tree[4]=BitmapFactory.decodeResource(getResources(),R.drawable.tree0);

     paint = new Paint();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        rectWidth=(dWidth+300);
        rect1X=rect1Back+supp1X;
        rect2X=rectWidth+rect2Back;
        rect1Width=rect1X+rectWidth;
        rect2Width=rect2X+rectWidth;

        birdRect=new Rect(-(dWidth+500),  -(dHeight+400+birdY) ,100,300+birdY);

        rect1=new Rect(rect1X,0,rect1Width,dHeight);
        rect2=new Rect(rect2X,0,rect2Width,dHeight);

        if(rect1Width==0){
            rect1Back=0;
            supp1X=rectWidth;
        }
        if(rect2Width==0){
            rect2Back=0;
        }

        if(birdIndex>=birds.length)
            birdIndex=0;


           canvas.drawBitmap(background,null,rect1,null);
           canvas.drawBitmap(background,null,rect2,null);
           canvas.drawBitmap(birds[birdIndex],null,birdRect ,null);

        velocity+=gravity;
        birdY=velocity;

        handler.postDelayed(runnable,UPDATE_MILLIS);
        birdIndex+=1;

        rect1Back-=5;
        rect2Back-=5;

        for(int i=0;i<treeCount;i++){
             treeRect[i]=new Rect(treeX[i],treeY[i],treeX[i]+1000,treeHeight[i]);
             canvas.drawBitmap(tree[i],null,treeRect[i],null);
             treeX[i]-=8;
        }

        if(treeX[treeCount-1]+200<0) {
            asignTreeX();

        }

        if((300+birdY)<=100){
            Log.d("onDraw: ",""+birdY);
            birdY=-200;
            velocity-=(velocity/2);
        }


       for(int i=0;i<treeCount;i++){
           if(new Rect(33, 100+birdY, 100, 200+birdY).intersect(new Rect(treeX[i],treeY[i]-(treeY[i]/2-150),treeX[i]+200,treeHeight[i]))){
              popUpDialog("Game Over");
           }
       }


    }

    private void popUpDialog(String val){
        final Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.dialog);
        dialog.setTitle("winning status");

        TextView text=dialog.findViewById(R.id.dialog_message);
        text.setText(val);

        TextView dialogButton=dialog.findViewById(R.id.ok);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                velocity=10;
            }
        });
        dialog.show();
        velocity=10;
        asignTreeX();
    }

    private void asignTreeX(){
        treeCount=getRandom(tree.length);
        if(treeCount==0)
            treeCount=2;


        for(int i=0;i<treeCount;i++){
            treeX[i]=getTreeX();
            treeY[i]=getTreeY();
            if(treeY[i]<(dHeight/2)+1000)
            treeHeight[i]=dHeight+2000;
            else
                treeHeight[i]=dHeight+500;

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action=event.getAction();
        if(action==MotionEvent.ACTION_DOWN){
            velocity-=(gravity+60);
        }

        return true;
    }

    private int getRandom(int value){
        return random.nextInt(value);
    }

    private int getTreeY(){
        int valX=dWidth+100;
        loop=true;
        while(loop) {
            valX=getRandom(2*dWidth);
            if(valX>(dHeight/2)-500&&valX<dHeight-500){
                loop=false;
                break;
            }
        }
            return valX;
    }

    private int getTreeX(){
        int valX=dWidth+100;
        loop=true;
        while(loop) {
            valX=getRandom((2*dWidth)-500);
            if(valX>dWidth){
                loop=false;
                break;
            }
        }
        return (getRandom(tree.length)*10)+valX;

    }

}
