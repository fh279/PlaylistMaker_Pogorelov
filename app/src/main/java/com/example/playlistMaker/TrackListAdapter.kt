package com.example.playlistMaker

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistMaker.mediaLibraryClasses.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackListAdapter(
    private val trackList: List<Track>
) : RecyclerView.Adapter<TrackListAdapter.TrackListViewHolder>() {

    val a = 10
    lateinit var tracks: List<Track>
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
            "${model.artistName} • ${convertMillisToMinutes(model.trackTime)}".also { artistAndTime.text = it }
            Glide
                .with(itemView)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(albumCover)
        }

        // Вот это нам понадобится для того что бы форматировать миллисекунды в минуты и секунды.
        private fun convertMillisToMinutes(millis: Long?): String {
            return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
        }
    }

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }
}