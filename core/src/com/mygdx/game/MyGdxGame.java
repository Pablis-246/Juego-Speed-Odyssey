package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.audio.Music;


public class MyGdxGame extends ApplicationAdapter {
    SpriteBatch batch;
    TiledMap map;
    OrthographicCamera camera;
    TiledMapRenderer mapRenderer;

    Viewport viewport;

    Player player;
    int tileWidth = 64; // Ancho de cada tile en píxeles
    int tileHeight = 64; // Alto de cada tile en píxeles
    int mapWidthInTiles = 35; // Ancho del mapa en tiles
    int mapHeightInTiles = 20; // Alto del mapa en tiles
    BitmapFont font;
    
    private Music backgroundMusic;
    
    @Override
    public void create() {
        batch = new SpriteBatch();

        //Pablo 28/7 crear mapa en tiled ejemplo de mario
        //Pablo 31/7 cargar mapa hecho en tiled
        
        // Cargar el mapa desde TiledMapEditor
        map = new TmxMapLoader().load("mapa2.1.tmx");
        camera = new OrthographicCamera();
        viewport = new StretchViewport(mapWidthInTiles * tileWidth, mapHeightInTiles * tileHeight,camera);

        camera.setToOrtho(false, mapWidthInTiles * tileWidth, mapHeightInTiles * tileHeight);
        camera.position.set(mapWidthInTiles * tileWidth / 2f, mapHeightInTiles * tileHeight / 2f, 0);
        camera.update();
        
        // Configurar el renderizador del mapa
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        
        //Pablo cargar personaje ej Mario se puede cambiar 1/8
        // Crear una instancia del jugador y establecer su posición inicial
        player = new Player((TiledMapTileLayer) map.getLayers().get(0),20,11f*50);
        
        // Inicializar la fuente para mostrar texto
        font = new BitmapFont();
        
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("musica-inicio.mp3"));
        backgroundMusic.setLooping(true); // Repetir la música
        backgroundMusic.setVolume(0.1f); // Ajustar el volumen (0.0 a 1.0)
        backgroundMusic.play(); // Reproducir la música
       
    }

    
    @Override
    public void render() {
        Gdx.gl.glClearColor(0.0f, 0.5f, 1.0f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Centrar la cámara en la posición del jugador y actualizarla
        camera.position.set(player.getX(),player.getY(),0);
        camera.update();
        
        // Configurar la matriz de proyección de batch con la cámara
        batch.setProjectionMatrix(viewport.getCamera().combined);
        
        // Configurar el renderizador de mapa para usar la cámara
        mapRenderer.setView(camera);
        mapRenderer.render();
        
        // Comenzar el dibujado de sprites
        batch.begin();
        
        // Configurar la escala de la fuente y mostrar instrucciones en la pantalla
        font.getData().setScale(5);
        font.draw(batch,"Controles",100,17*50);
        font.draw(batch,"A - D - Space",100,15.5f*50);
        font.draw(batch,"LEFT - RIGHT - Space",100,14*50);

     
        player.checkForBonusCollision(); // Llama a la función de verificación de colisión de bonus

        
        // Renderizar al jugador
        player.render(batch);

        // Finalizar el dibujado de sprites
        batch.end();
    }
    
    @Override
    public void dispose() {
        // Libera los recursos cuando ya no se necesiten
        batch.dispose();
        map.dispose();
        
     // Liberar los recursos de música
        backgroundMusic.dispose();
        
    }

    // Método para redimensionar la ventana
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}