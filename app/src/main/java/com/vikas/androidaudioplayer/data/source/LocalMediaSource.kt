package com.vikas.androidaudioplayer.data.source

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.vikas.androidaudioplayer.domain.model.AudioTrack
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocalMediaSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getAudioTracks(): List<AudioTrack> {
        val tracks = mutableListOf<AudioTrack>()
        val collection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA, // Path
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.MIME_TYPE
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val albumId = cursor.getLong(albumIdColumn)
                
                val albumArtUri = ContentUris.withAppendedId(
                    android.net.Uri.parse("content://media/external/audio/albumart"),
                    albumId
                ).toString()

                tracks.add(
                    AudioTrack(
                        id = id.toString(),
                        title = cursor.getString(titleColumn) ?: "Unknown",
                        artist = cursor.getString(artistColumn) ?: "Unknown Artist",
                        album = cursor.getString(albumColumn) ?: "Unknown Album",
                        albumArtist = null, // Not easily available in basic query
                        duration = cursor.getLong(durationColumn),
                        path = cursor.getString(pathColumn),
                        albumArtUri = albumArtUri,
                        trackNumber = cursor.getInt(trackColumn),
                        year = cursor.getInt(yearColumn),
                        genre = null, // Requires separate query or different projection often
                        bitrate = null,
                        sampleRate = null,
                        dateAdded = cursor.getLong(dateAddedColumn),
                        dateModified = cursor.getLong(dateModifiedColumn),
                        size = cursor.getLong(sizeColumn),
                        mimeType = cursor.getString(mimeTypeColumn) ?: "audio/*"
                    )
                )
            }
        }
        return tracks
    }
}
