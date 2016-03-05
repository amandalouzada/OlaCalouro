package louzada.olacalouro.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import louzada.olacalouro.Dijkstra;
import louzada.olacalouro.Grafo;
import louzada.olacalouro.OlaCalouroDAO;
import louzada.olacalouro.R;
import louzada.olacalouro.domain.Local;
import louzada.olacalouro.domain.Vertice;

public class MapaUftActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private OlaCalouroDAO dao;
    private Grafo grafo;
    private Dijkstra dijkstra;
    private Polyline polyline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_uft);

        dao = new OlaCalouroDAO(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // Add a marker in Sydney and move the camera
        LatLng uft = new LatLng(-10.178709, -48.360024);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uft, 17));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        adicionarMarcadores();
    }

    public void adicionarMarcadores(){

        List<Local> listaLocais = dao.listarLocais();

        for (Local local : listaLocais) {
            LatLng posicaoMarcador = new LatLng(local.getLat(), local.getLng());
            mMap.addMarker(new MarkerOptions().position(posicaoMarcador).title(local.getNome()).snippet(local.getDescricao()));

        }

    }

    public void popularGrafo() {
        grafo = new Grafo(dao.listarVertices(), dao.listarArestas());
    }

    public void popularDijkstra() {
        dijkstra = new Dijkstra(grafo);
    }

    public void desenharRota(){

        popularGrafo();
        popularDijkstra();

        dijkstra.execute(dao.buscarVerticePorId((long) 10));
        LinkedList<Vertice> caminho = dijkstra.getPath(dao.buscarVerticePorId((long) 13));
        PolylineOptions polylineOptions = new PolylineOptions();

        for(Vertice v : caminho) {

            LatLng coordenada = new LatLng(v.getLat(), v.getLng());
            polylineOptions.add(coordenada);
        }

        polylineOptions.color(Color.BLUE).width(5);
        polyline = mMap.addPolyline(polylineOptions);
    }

}
