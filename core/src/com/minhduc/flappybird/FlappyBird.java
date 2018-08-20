package com.minhduc.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;

	Texture background;
	Texture gameover;
	Texture[] birds;
	Texture topTube;
	Texture bottomTube;

	Circle birdCircle;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;

	BitmapFont font;

	ShapeRenderer shapeRenderer;

	int flapState = 0;
	int gameState = 0;
	int numberOfTube = 4;
	int score = 0;
	int scoringTube = 0;

	float gravity = 1;
	float birdY = 0;
	float velocity = 0;

	float gap = 380;
	float[] tubeOffset = new float[numberOfTube];
	float maxTubeOffset;
	float tubeVelocity = 4;
	float[] tubeX = new float[numberOfTube];
	float distanceBetweenTubes;

	Random randomGenerator;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameover = new Texture("gameover.png");

		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");

		birdCircle = new Circle();
		topTubeRectangles = new Rectangle[numberOfTube];
		bottomTubeRectangles = new Rectangle[numberOfTube];

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(12);

		shapeRenderer = new ShapeRenderer();

		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 5;
		randomGenerator = new Random();

		startGame();
	}

	public void startGame(){
		birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;
		for(int i = 0; i < numberOfTube; i++) {
			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() / 2 + i * distanceBetweenTubes;
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 1) {
			if(Gdx.input.justTouched()) {
				velocity = -21;
			}

			for(int i = 0; i < numberOfTube; i++) {
				if(tubeX[i] < -topTube.getWidth()){
					tubeX[i] += numberOfTube * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				} else {
					tubeX[i] -= tubeVelocity;
					if(tubeX[scoringTube] < Gdx.graphics.getWidth() / 2){
						score++;
						Gdx.app.log("Score", String.valueOf(score));

						if(scoringTube < numberOfTube - 1){
							scoringTube++;
						}
						else {
							scoringTube = 0;
						}
					}
				}

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i] + 5, topTube.getWidth() - 10, topTube.getHeight() - 10);
				bottomTubeRectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight() / 2 - bottomTube.getHeight() - gap / 2 + tubeOffset[i] - 5, bottomTube.getWidth() - 10, bottomTube.getHeight() - 10);
			}

			if(birdY > 0 && birdY < Gdx.graphics.getHeight() + gap / 2) {
				velocity += gravity;
				birdY -= velocity;
			} else {
			    gameState = 2;
            }
		}
		else if(gameState == 0){
            if(Gdx.input.justTouched()) {
                Gdx.app.log("Touched", "Yep!");
                gameState = 1;
            }
        }
        else if(gameState == 2){
			if(birdY > birds[flapState].getHeight() / 4) {
				velocity += gravity;
				birdY -= velocity;
			}
		    batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2);
			if(Gdx.input.justTouched() && birdY < birds[flapState].getHeight() / 4) {
				Gdx.app.log("Touched", "Restart!");
				startGame();
				gameState = 0;
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
        }

        if (flapState == 0 && gameState == 1) {
            flapState = 1;
        } else {
            flapState = 0;
        }

        batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
		font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth() / 2 - font.getXHeight() / 2, Gdx.graphics.getHeight() * 9 / 10);
        batch.end();

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.BROWN);

        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);
//        shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

        for(int i = 0; i < numberOfTube; i++) {
//			shapeRenderer.rect(topTubeRectangles[i].x, topTubeRectangles[i].y, topTubeRectangles[i].width, topTubeRectangles[i].height);
//			shapeRenderer.rect(bottomTubeRectangles[i].x, bottomTubeRectangles[i].y, bottomTubeRectangles[i].width, bottomTubeRectangles[i].height);

			if(Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])){
				Gdx.app.log("Collision", "Game Over!");
				gameState = 2;
			}
		}

//        shapeRenderer.end();

	}

//	@Override
//	public void dispose () {
//		batch.dispose();
//		background.dispose();
//
//	}
}
