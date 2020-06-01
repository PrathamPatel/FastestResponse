package com.example.fastestresponse;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

//Implement SensorEventListener to make use of the senors on device for detecting tilts
public class SinglePlayer extends AppCompatActivity implements SensorEventListener {

    final Random random = new Random();

    TextView countdownTv;
    TextView curr_round;
    TextView score;
    ImageView arrow;

    private int countdown = 3;
    private int current_round = 1;
    private int current_score = 0;
    private int current_round_direction = 0;
    private boolean active_round = false;
    //non default direction
    private int curr_player_direction = NO_DIRECTION;

    //rotation arrow degrees
    public static int LEFT_ROTATION = 0;
    public static int UP_ROTATION = 90;
    public static int RIGHT_ROTATION = 180;
    public static int DOWN_ROTATION = 270;

    public static int LEFT_DIRECTION = 0;
    public static int UP_DIRECTION = 1;
    public static int RIGHT_DIRECTION = 2;
    public static int DOWN_DIRECTION = 3;
    public static int NO_DIRECTION = -1;

    private int[] rotations = {LEFT_ROTATION, UP_ROTATION, RIGHT_ROTATION, DOWN_ROTATION};

    //Upon doing research, some sensor related code was adapted from the SensorProcessorActivity.java file found at
    //https://github.com/bazilio91/android-grass-cutter
    private SensorManager mSensorManager = null;
    private float[] magnet = new float[3];
    private float[] accel = new float[3];
    protected float[] accMagOrientation = new float[3];
    private float[] rotationMatrix = new float[9];

    private WhichColour whichColourType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_single_player);

        countdownTv = findViewById(R.id.countDown);
        arrow = findViewById(R.id.arrow);
        curr_round = findViewById(R.id.roundNum);
        score = findViewById(R.id.scoreNum);

        whichColourType = WhichColour.getInstance(getApplicationContext());
        //set colour of arrow
        arrow.setColorFilter(whichColourType.getCurrentColourId());

        curr_round.setVisibility(View.GONE);
        score.setVisibility(View.GONE);

        beginCountdown();

    }

    private void updateScoreAndRound(){

        score.setText("SCORE: "+ current_score);
        curr_round.setText("ROUND: " + current_round);
    }

    private void beginCountdown(){
        new CountDownTimer(countdown * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                countdown -= 1;
                countdownTv.setText(String.valueOf(countdown + 1));

            }
            public void onFinish() {
                countdownTv.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Game has Begun!" , Toast.LENGTH_SHORT).show();
                letsStartTheGame();
            }
        }.start();
    }

    private void letsStartTheGame() {
        updateScoreAndRound();
        score.setVisibility(View.VISIBLE);
        curr_round.setVisibility(View.VISIBLE);
        letsStartTheRound();
    }

    private void letsStartTheRound() {
        int maxRounds = 10;
        if (current_round <= maxRounds) {

            int i1 = random.nextInt(4);//get any random number
            arrow.setRotation(rotations[i1]);//set any random rotation
            arrow.setVisibility(View.VISIBLE);
            current_round_direction = i1;

            //set random interval per round
            int intervals = random.nextInt(5 - 2 + 1) + 2;

            //Set active_round as true and start count down timer based on the random number generated.
            active_round = true;
            //Check if sensor direction has changed every 0.1 seconds
            CountDownTimer current_roundTimer = new CountDownTimer(intervals * 1000, 100) {
                @Override
                public void onTick(long l) {
                    //detect change in direction
                    if (curr_player_direction != NO_DIRECTION) {
                        playerController();
                    }
                }

                @Override
                public void onFinish() {
                    //set active round to false and update the score and round values and wait
                    //for the next round.
                    active_round = false;
                    updateScoreAndRound();
                    arrow.setVisibility(View.GONE);
                    current_round++;
                    letsStartTheRound();
                }
            }.start();

        } else {
            gameHasEnded();
        }

    }

    private void playerController() {

        if(active_round) {

            //if user made a valid tilt, we update score value
            if (current_round_direction == curr_player_direction) {

                current_score++;
                active_round = false;
                arrow.setVisibility(View.GONE);
            }

        } else {

            //if the tilt was not needed between the rounds, we minus the score
            if (curr_player_direction != NO_DIRECTION) {
                Toast.makeText(getApplicationContext(), "UNEXPECTED TILT - DECREASING SCORE" ,
                        Toast.LENGTH_SHORT).show();
                current_score--;
            }
        }
       //after the tilt we give some time for user to return to normal position.
        waitToReturnToNormalPostion();

        updateScoreAndRound();
    }

    private void waitToReturnToNormalPostion() {
        mSensorManager.unregisterListener(this);
        //we set direction back to none.
        curr_player_direction = NO_DIRECTION;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                registerSensors();
            }
        }, 500);
    }

    
    private void registerSensors() {
        //we set the listeners to use the sensors during gameplay
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    //unregister sensor to save battery
    @Override
    public void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    //unregister sensor to save battery
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    //re-register sensors to use in gameplay
    @Override
    public void onResume() {
        super.onResume();
        registerSensors();
    }

    private void gameHasEnded() {
        //we save the score and pass it onto the finished game and final score activity and destroy
        //the current activity.
        int finalScore = current_score;
        Intent i = new Intent(SinglePlayer.this, FinalScore.class);
        i.putExtra("score",finalScore);
        startActivity(i);

        finish();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Must be implemented to satisfy the SensorEventListener interface;
    }

    //The following sensor related methods have been taken and used from:
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

       switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                // copy new accelerometer data into accel array and calculate orientation
                System.arraycopy(sensorEvent.values, 0, accel, 0, 3);
                calculateAccMagOrientation(sensorEvent.values);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                // copy new magnetometer data into magnet array
                System.arraycopy(sensorEvent.values, 0, magnet, 0, 3);
                break;
        }
    }

    //Code at : https://stackoverflow.com/questions/38711705/android-device-orientation-without-geomagnetic
    //device orientation without geomagnetic
    private void calculateAccMagOrientation(float[] values) {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)) {
            SensorManager.getOrientation(rotationMatrix, accMagOrientation);
        } else {
            double gx, gy, gz;
            gx = accel[0] / 9.81f;
            gy = accel[1] / 9.81f;
            gz = accel[2] / 9.81f;

            //http://theccontinuum.com/2012/09/24/arduino-imu-pitch-roll-from-accelerometer/
            float pitch = (float) -Math.atan(gy / Math.sqrt(gx * gx + gz * gz));
            float roll = (float) -Math.atan(gx / Math.sqrt(gy * gy + gz * gz));
            float azimuth = 0; // This value is impossible to guess, only calculated

            accMagOrientation[0] = azimuth;
            accMagOrientation[1] = pitch;
            accMagOrientation[2] = roll;
            accMagOrientation = getRotationMatrixFromOrientation(accMagOrientation);
        }

        //https://github.com/kevinvanzyl/showdown-at-high-noon/blob/master/app/src/main/java/codes/kevinvanzyl/showdownathighnoon/sensors/TiltSensor.java
        float x = values[0];
        float y = values[1];
        if (Math.abs(x) > Math.abs(y)) {
            if (x < 0) {
                if (Math.toDegrees(accMagOrientation[2]) >= 50) {
                    curr_player_direction = DOWN_DIRECTION;
                }
            }
            if (x > 0) {
                if (Math.toDegrees(accMagOrientation[2]) <= -50) {
                    curr_player_direction = UP_DIRECTION;
                }
            }
        } else {
            if (y < 0) {
                if (Math.toDegrees(accMagOrientation[1]) >= 50) {
                    curr_player_direction = RIGHT_DIRECTION;
                }
            }
            if (y > 0) {
                if (Math.toDegrees(accMagOrientation[1]) <= -50) {
                    curr_player_direction = LEFT_DIRECTION;
                }
            }
        }
        if (x > (-2) && x < (2) && y > (-2) && y < (2)) {
            curr_player_direction = NO_DIRECTION;
        }
    }

    //as the method suggests we get the rotation matrix of how much the device orientation has
    //changed
    private float[] getRotationMatrixFromOrientation(float[] o) {

        float[] xM = new float[9];
        float[] yM = new float[9];
        float[] zM = new float[9];

        float sinX = (float) Math.sin(o[1]);
        float cosX = (float) Math.cos(o[1]);
        float sinY = (float) Math.sin(o[2]);
        float cosY = (float) Math.cos(o[2]);
        float sinZ = (float) Math.sin(o[0]);
        float cosZ = (float) Math.cos(o[0]);

        // rotation about x-axis (pitch)
        xM[0] = 1.0f;
        xM[1] = 0.0f;
        xM[2] = 0.0f;
        xM[3] = 0.0f;
        xM[4] = cosX;
        xM[5] = sinX;
        xM[6] = 0.0f;
        xM[7] = -sinX;
        xM[8] = cosX;

        // rotation about y-axis (roll)
        yM[0] = cosY;
        yM[1] = 0.0f;
        yM[2] = sinY;
        yM[3] = 0.0f;
        yM[4] = 1.0f;
        yM[5] = 0.0f;
        yM[6] = -sinY;
        yM[7] = 0.0f;
        yM[8] = cosY;

        // rotation about z-axis (azimuth)
        zM[0] = cosZ;
        zM[1] = sinZ;
        zM[2] = 0.0f;
        zM[3] = -sinZ;
        zM[4] = cosZ;
        zM[5] = 0.0f;
        zM[6] = 0.0f;
        zM[7] = 0.0f;
        zM[8] = 1.0f;

        // rotation order is y, x, z (roll, pitch, azimuth)
        float[] resultMatrix = matrixMultiplication(xM, yM);
        resultMatrix = matrixMultiplication(zM, resultMatrix);
        return resultMatrix;
    }

    private float[] matrixMultiplication(float[] A, float[] B) {

        float[] result = new float[9];

        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];

        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];

        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];

        return result;
    }


}