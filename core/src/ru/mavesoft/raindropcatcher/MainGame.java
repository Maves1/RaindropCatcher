package ru.mavesoft.raindropcatcher;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class MainGame extends ApplicationAdapter {
    int SCREEN_WIDTH;
    int SCREEN_HEIGHT;

    private SpriteBatch spriteBatch;
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music bgMusic;

	private Rectangle bucketRect;

	private Array<Rectangle> rainDrops;
	private long timeLastDropped;

	private long spawnFrequency;

	private BitmapFont scoreFont;
	private int score;
	
	@Override
	public void create () {
	    SCREEN_WIDTH = Gdx.graphics.getWidth();
	    SCREEN_HEIGHT = Gdx.graphics.getHeight();
	    spawnFrequency = 1000000000l;

	    rainDrops = new Array<Rectangle>();

	    spriteBatch = new SpriteBatch();
	    dropImage = new Texture("droplet.png");
	    bucketImage = new Texture("bucket.png");
	    dropSound = Gdx.audio.newSound(Gdx.files.internal("dropsound.wav"));
	    bgMusic = Gdx.audio.newMusic(Gdx.files.internal("bgmusic.mp3"));

	    bucketRect = new Rectangle();
	    bucketRect.x = SCREEN_WIDTH / 2 - bucketImage.getWidth() / 2;
	    bucketRect.y = 20;
	    bucketRect.width = bucketImage.getWidth();
	    bucketRect.height = bucketImage.getHeight();

	    bgMusic.setLooping(true);
	    bgMusic.play();

	    createADrop();

	    scoreFont = new BitmapFont();
	    scoreFont.getData().setScale(3);
	    scoreFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
	    score = 0;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0.2f, 0.3f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (Gdx.input.isTouched()) {
            bucketRect.x = Gdx.input.getX() - bucketImage.getWidth() / 2;
		    if (bucketRect.x <= 0) {
                bucketRect.x = 0;
            } else if (bucketRect.x >= SCREEN_WIDTH) {
		        bucketRect.x = SCREEN_WIDTH - bucketImage.getWidth();
            }

        }

        if (TimeUtils.nanoTime() - timeLastDropped >= spawnFrequency) {
            createADrop();
        }

        for (Iterator<Rectangle> iter = rainDrops.iterator(); iter.hasNext(); ) {
            Rectangle drop = iter.next();
            drop.y -= 100 * Gdx.graphics.getDeltaTime();
            if (drop.y <= 0 - dropImage.getHeight()) {
                iter.remove();
            }
            if(drop.overlaps(bucketRect)) {
                dropSound.play();
                score++;
                iter.remove();
            }
        }

		spriteBatch.begin();
		spriteBatch.draw(bucketImage, bucketRect.x, bucketRect.y);
		for (Rectangle drop : rainDrops) {
		    spriteBatch.draw(dropImage, drop.x, drop.y);
        }
        scoreFont.draw(spriteBatch, Integer.toString(score), SCREEN_WIDTH / 2, SCREEN_HEIGHT - 100);
		spriteBatch.end();
	}
	
	@Override
	public void dispose () {
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        bgMusic.dispose();
        spriteBatch.dispose();
        scoreFont.dispose();
	}

	public void createADrop () {
	    Rectangle dropRect = new Rectangle();
	    dropRect.width = dropImage.getWidth();
	    dropRect.height = dropImage.getHeight();
	    dropRect.x = MathUtils.random(0, SCREEN_WIDTH - dropRect.width);
	    dropRect.y = SCREEN_HEIGHT;
	    rainDrops.add(dropRect);
	    timeLastDropped = TimeUtils.nanoTime();
    }
}
