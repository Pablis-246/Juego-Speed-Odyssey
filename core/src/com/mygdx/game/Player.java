package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.audio.Sound;

import java.util.Arrays;

//Pablo Configuracion del personaje teclas gravedad 2/8
//Pablo colisiones 4/8

public class Player {
	// Atributos del jugador
    private Texture playerTexture;
    private Vector2 position;
    private Vector2 velocity;
    private float gravity;
    private Rectangle collisionRectangle;
    private boolean isJumping;
    

    // Constantes para el movimiento y la física
    private static final float MOVEMENT_SPEED = 15f;
    private static final float MAX_SPEED = 30f;
    private static final float JUMP_VELOCITY = 600f;
    private static final float GRAVITY = -5f;

    // Animaciones y texturas
    private TextureRegion[] regionsMovimiento;
    private Texture imagen;
    private TextureRegion frameActual;
    private Animation<TextureRegion> animation;
    private float tiempo = 0f;
    TiledMapTileLayer collisionLayer;
    private int TILE_SIZE;
    
    //Efectos para el jugador
    private Sound jumpSound;
    private Sound collisionSound;
    private boolean hasCollided;
    
    private Sound bonusSound1;


    private boolean invertedGravity;

    //Constructor
    public Player(TiledMapTileLayer collisionLayer, float x, float y) {
        invertedGravity = false;
        this.collisionLayer = collisionLayer;
        TILE_SIZE = collisionLayer.getTileWidth();
        imagen = new Texture(Gdx.files.internal("marioSprite.png"));
        TextureRegion [][] tmp = TextureRegion.split(imagen,imagen.getWidth()/6,imagen.getHeight());
        regionsMovimiento = new TextureRegion[6];      
        for (int i = 0; i < 6; i++) {
        	regionsMovimiento[i] = tmp[0][i];
        }       
        animation = new Animation(1/10f,  Arrays.copyOfRange(regionsMovimiento,1,regionsMovimiento.length));
        playerTexture = imagen;
        position = new Vector2(x, y);
        velocity = new Vector2();
        gravity = GRAVITY;
        collisionRectangle = new Rectangle(x, y, playerTexture.getWidth(), playerTexture.getHeight());
        isJumping = false;
        
        //efectos de sonido
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("efecto-salto.mp3"));
        collisionSound = Gdx.audio.newSound(Gdx.files.internal("efecto-choque.mp3")); 
        hasCollided = false; // Inicializar la variable de estado

        bonusSound1 = Gdx.audio.newSound(Gdx.files.internal("efecto-bonus.mp3"));

        
    }

    // Método para actualizar el jugador
    public void update(SpriteBatch batch, float deltaTime) {
        float XSpeed = 0;

        // Detectar entrada del teclado para el movimiento horizontal
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            XSpeed -= MOVEMENT_SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            XSpeed += MOVEMENT_SPEED;
        }

        // Cambiar la gravedad invertida al presionar la tecla SPACE
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            invertedGravity = !invertedGravity;
         // Reproducir el sonido de salto al presionar SPACE
            jumpSound.play();
        }

        // Guardar la posición original antes de la posible colisión
        float oldX = position.x;
        float oldY = position.y;
        boolean collideX, collideY;

        // Mover en el eje X
        position.x += XSpeed;
        collideX = collidesLeft() || collidesRight();
        if (collideX) {
            position.x = oldX;
        }

        // Mover en el eje Y (con gravedad invertida)
        position.y += (invertedGravity ? -gravity : gravity);
        collideY = collidesBottom() || collidesTop();
        if (collideY) {
            position.y = oldY;
        }

        // Actualizar el tiempo para la animación
        tiempo += Gdx.graphics.getDeltaTime();

        // Determinar la textura actual para la animación
        TextureRegion texture;
        if (XSpeed == 0) {
            texture = regionsMovimiento[0];
        } else {
            texture = animation.getKeyFrame(tiempo, true);
        }

        // Determinar si se debe voltear la textura
        boolean flipX = false;
        boolean flipY = false;
        if ((XSpeed < 0 && !texture.isFlipX()) || (XSpeed > 0 && texture.isFlipX())) {
            flipX = true;
        }
        if (invertedGravity != texture.isFlipY()) {
            flipY = true;
        }

        // Aplicar el volteo a la textura
        texture.flip(flipX, flipY);
        frameActual = texture;

        // Dibujar el personaje en la posición actual
        batch.draw(frameActual, position.x, position.y, 100, 100);
    }


    // Otros métodos de movimiento y lógica
    public void moveForward() {
        velocity.x = MOVEMENT_SPEED;
    }

    public void stopMoving() {
        velocity.x = 0;
    }

    public void jump() {
        if (!isJumping) {
            velocity.y = JUMP_VELOCITY;
            isJumping = true;
      
        }
    }

    public void render(SpriteBatch batch) {
        update(batch,Gdx.graphics.getDeltaTime());
    }

    // Métodos de colisión
    private boolean isCellBlocked(float x, float y) {
        TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()));
        if(cell != null && cell.getTile() != null ){
            
        }
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("solid");
    }
    
    public void checkForBonusCollision() {
        int playerTileX = (int) (getX() / TILE_SIZE); // Suponiendo que TILE_SIZE es el tamaño de tus cuadrados en el mapa
        int playerTileY = (int) (getY() / TILE_SIZE);
        
        if ((playerTileX == 31 && playerTileY == 7) || (playerTileX == 54 && playerTileY == 7) || (playerTileX == 77 && playerTileY == 11)) {

            // Reproduce el sonido de bonus 1
            bonusSound1.play();
        }
 
    }


    public boolean collidesRight() {
        for(float step = 0; step < getHeight(); step += collisionLayer.getTileHeight() / 2)
            if(isCellBlocked(getX() + getWidth(), getY() + step)) {         	
                collisionSound.play(0.2f); //ajuste de sonido
                hasCollided = true; // Establecer la variable de estado en true para evitar repeticiones
                return true;
            }
               
        return false;
    }

    public boolean collidesLeft() {
        for(float step = 0; step < getHeight(); step += collisionLayer.getTileHeight() / 2)
            if(isCellBlocked(getX(), getY() + step)) {

            	return true;
            }
                
        return false;
    }

    public boolean collidesTop() {
        for(float step = 0; step < getWidth(); step += collisionLayer.getTileWidth() / 2)
            if(isCellBlocked(getX() + step, getY() + getHeight())) {

            	return true;
            }
                
        return false;
    }

    public boolean collidesBottom() {
        for(float step = 0; step < getWidth(); step += collisionLayer.getTileWidth() / 2)
            if(isCellBlocked(getX() + step, getY() + 1.5f*step)) {

            	return true;
            }
        
        return false;
    }

    // Métodos de obtención de información del jugador
    public float getWidth(){
        return this.playerTexture.getWidth();
    }
    public float getHeight(){
        return this.playerTexture.getHeight();
    }
    public float getX(){
        return position.x;
    }
    public float getY(){
        return position.y;
    }
}

