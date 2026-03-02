package com.example.playlistMaker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistMaker.mediaLibraryClasses.MockedTracks
import com.example.playlistMaker.mediaLibraryClasses.Track

class MediaLibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medialibrary)
        enableEdgeToEdge()

        val trackList = findViewById<RecyclerView>(R.id.track_list_recycler_view)
        trackList.layoutManager = LinearLayoutManager(
            /*context = */this,
            /* orientation = */ RecyclerView.VERTICAL,
            /* reverseLayout = */ false
        )

        val tracks = MockedTracks.listOfTracks




    }
}

class TrackListAdapter(
    private val trackList: List<Track>
) : RecyclerView.Adapter<TrackListViewHolder> () {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_list_item, parent, false)
        return TrackListViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TrackListViewHolder,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

}











class TrackListViewHolder(tracklistItemView: View) : RecyclerView.ViewHolder(tracklistItemView) {
    /*val trackName: String =
    val artistName: String =
    val trackTime: String =
    val artworkUrl100: String =

    init {

    }*/
    fun bind(model: Track) {

    }
}
