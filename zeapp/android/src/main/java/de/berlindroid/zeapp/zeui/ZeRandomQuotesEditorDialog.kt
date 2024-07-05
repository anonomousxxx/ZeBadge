@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package de.berlindroid.zeapp.zeui

import android.app.Activity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import de.berlindroid.zeapp.R
import de.berlindroid.zeapp.zebits.composableToBitmap
import de.berlindroid.zeapp.zebits.isBinary
import de.berlindroid.zeapp.zemodels.ZeConfiguration
import de.berlindroid.zeapp.zeui.zepages.RandomQuotePage
import de.berlindroid.zeapp.zeui.zetheme.ZeBlack
import de.berlindroid.zeapp.zeui.zetheme.ZeWhite
import kotlin.random.Random

/**
 * Editor dialog for selecting the quote of the day
 *
 * @param config configuration of the slot, containing details to be displayed
 * @param dismissed callback called when dialog is dismissed / cancelled
 * @param accepted callback called with the new configuration configured.
 * @param updateMessage show a new message to the user.
 */
@Composable
fun RandomQuotesEditorDialog(
    config: ZeConfiguration.Quote,
    dismissed: () -> Unit = {},
    accepted: (config: ZeConfiguration.Quote) -> Unit,
    updateMessage: (String) -> Unit,
) {
    val activity = LocalContext.current as Activity

    var message by remember { mutableStateOf(config.message) }
    var author by remember { mutableStateOf(config.author) }
    var image by remember { mutableStateOf(config.bitmap) }

    fun redrawComposableImage() {
        composableToBitmap(
            activity = activity,
            content = {
                RandomQuotePage(message, author)
            },
        ) {
            image = it
        }
    }

    AlertDialog(
        containerColor = ZeWhite,
        onDismissRequest = dismissed,
        confirmButton = {
            Button(
                onClick = {
                    if (image.isBinary()) {
                        accepted(ZeConfiguration.Quote(message, author, image))
                    } else {
                        updateMessage(activity.resources.getString(R.string.binary_image_needed))
                    }
                },
            ) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            Button(onClick = dismissed) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
        title = {
            Text(
                color = ZeBlack,
                text = stringResource(R.string.click_get_to_show_quote_of_the_day),
            )
        },
        properties = DialogProperties(),
        text = {
            LazyColumn {
                item {
                    BinaryImageEditor(
                        bitmap = image,
                        bitmapUpdated = { image = it },
                    )
                }

                item {
                    Button(onClick = {
                        val index = Random.nextInt(0, 10)
                        message = quoteList[index].q
                        author = quoteList[index].a
                        redrawComposableImage()
                    },) {
                        Text(text = stringResource(R.string.get))
                    }
                }
            }
        },
    )
}

data class Quote(
    val q: String,
    val a: String,
)

private val quoteList = listOf(
    Quote("You win more from losing than winning.", "Morgan Wootten"),
    Quote("The Only Thing That Is Constant Is Change", "Heraclitus"),
    Quote("Once you choose hope, anything's possible.", "Christopher Reeve"),
    Quote("The love of money is the root of all evil.", "the Bible"),
    Quote("The only thing we have to fear is fear itself.", "Franklin D. Roosevelt"),
    Quote("Whoever is happy will make others happy too.", "Anne Frank"),
    Quote("The purpose of our lives is to be happy", "Dalai Lama"),
    Quote("Only a life lived for others is a life worthwhile.", "Albert Einstein"),
    Quote("Live in the sunshine, swim the sea, drink the wild air.", "Ralph Waldo Emerson"),
    Quote("Life is trying things to see if they work.", "Ralph Waldo Emerson"),
)
