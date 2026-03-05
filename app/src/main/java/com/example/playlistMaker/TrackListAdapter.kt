package com.example.playlistMaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistMaker.mediaLibraryClasses.Track

class TrackListAdapter(
    private val trackList: List<Track>
) : RecyclerView.Adapter<TrackListAdapter.TrackListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    /* resource = */ R.layout.track_list_item,
                    /* root = */ parent,
                    /* attachToRoot = */ false
                )
        return TrackListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackListViewHolder, position: Int) {
        holder.bind(model = trackList[position])
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    class TrackListViewHolder(tracklistItemView: View) :
        RecyclerView.ViewHolder(tracklistItemView) {
        val albumCover = tracklistItemView.findViewById<ImageView>(R.id.album_cover)
        val songTitle = tracklistItemView.findViewById<TextView>(R.id.song_title)
        val artistAndTime = tracklistItemView.findViewById<TextView>(R.id.song_artist_and_time)
        val btnForward = tracklistItemView.findViewById<ImageView>(R.id.item_icon_forward)

        fun bind(model: Track) {
            songTitle.text = model.trackName
            "${model.artistName} • ${model.trackTime}".also { artistAndTime.text = it }
            Glide
                .with(itemView)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(albumCover)
        }
    }
}