package com.craftworks.music.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.craftworks.music.R
import com.craftworks.music.data.Playlist

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistCard(playlist: Playlist, onClick: () -> Unit){
    Card(
        onClick = { onClick() },
        modifier = Modifier.padding(12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 128.dp)
                //.width(128.dp)
                //.height(172.dp)
            , horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AsyncImage(
                model = playlist.coverArt,
                placeholder = painterResource(R.drawable.placeholder),
                fallback = painterResource(R.drawable.placeholder),
                contentScale = ContentScale.FillWidth,
                contentDescription = "Album Image",
                modifier = Modifier
                    .fillMaxSize()
                    //.height(128.dp)
                    //.width(128.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxSize().wrapContentHeight(align = Alignment.CenterVertically),
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}