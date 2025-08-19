package com.hihihihi.gureumpage.ui.bookdetail.components

import android.util.Log
import android.widget.RatingBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.gowtham.ratingbar.StepSize
import com.hihihihi.gureumpage.R
import com.hihihihi.gureumpage.designsystem.components.GureumTextField
import com.hihihihi.gureumpage.designsystem.components.Semi14Text
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme

@Composable
fun ReviewSection(
    initialRating: Float = 0f,
    initialReview: String = "",
    onSave: (Double, String) -> Unit = { _, _ -> }
) {
    var rating by remember { mutableStateOf(initialRating) }
    var reviewText by remember { mutableStateOf(initialReview) }
    var isEditMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp).padding(bottom =40.dp)
    ) {
        Semi14Text(
            text = "감상문",
            color = GureumTheme.colors.gray800,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RatingBar(
                value = rating,
                style = RatingBarStyle.Fill(),
                spaceBetween = 2.dp,
                stepSize = StepSize.HALF,
                size = 24.dp, // 별 크기 조정
                onValueChange = { newRating ->
                    if (isEditMode) {
                        rating = newRating
                    }
                },
                onRatingChanged = {
                    Log.d("TAG", "onRatingChanged: $it")
                }
            )

            IconButton(
                onClick = {
                    if (isEditMode) {
                        onSave(rating.toDouble(), reviewText)
                    }
                    isEditMode = !isEditMode
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(
                        if (isEditMode) R.drawable.ic_save_outline
                        else R.drawable.ic_pen_outline
                    ),
                    contentDescription = if (isEditMode) "저장" else "편집",
                    tint = if (isEditMode) GureumTheme.colors.primary
                    else GureumTheme.colors.gray500,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        GureumTextField(
            value = reviewText,
            onValueChange = {
                if (isEditMode) {
                    reviewText = it
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isEditMode,
            hint = "감상문을 작성해보세요!",
            minLines = 6,
            maxLines = 6,
            imeAction = ImeAction.Done
        )
    }
}