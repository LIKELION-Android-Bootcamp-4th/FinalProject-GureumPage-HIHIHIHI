package com.hihihihi.gureumpage.designsystem.components

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hihihihi.gureumpage.designsystem.theme.GureumPageTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTheme
import com.hihihihi.gureumpage.designsystem.theme.GureumTypography

/**
 * 스크린 목차 타이틀, 책 상세 책 제목, 마이페이지 버튼 리스트 등
 */
@Composable
fun Semi16Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GureumTheme.colors.gray900,
    style: TextStyle = GureumTypography.titleLarge,
    maxLine: Int = 2,
    isUnderline: Boolean = false,
    textAlign: TextAlign = TextAlign.Justify
) {
    val underlineColor: Color = GureumTheme.colors.primary50
    val underlineModifier = if (isUnderline) {
        Modifier.drawBehind {
            val underlineHeight = 9.dp.toPx()
            drawRect(
                color = underlineColor,
                topLeft = Offset(0f, size.height - underlineHeight),
                size = androidx.compose.ui.geometry.Size(width = size.width, underlineHeight),
            )
        }
    } else {
        Modifier
    }
    Text(
        text = text,
        color = color,
        style = style,
        maxLines = maxLine,
        overflow = TextOverflow.Ellipsis,
        textAlign = textAlign,
        modifier = underlineModifier.then(modifier),
    )
}

@Composable
fun Semi40Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GureumTheme.colors.gray800,
    style: TextStyle = GureumTypography.displayLarge,
    maxLine: Int = 4,
) {
    Text(
        text = text,
        color = color,
        style = style,
        maxLines = maxLine,
        modifier = modifier,
        textAlign = TextAlign.Justify
    )
}

@Composable
fun Medi32Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GureumTheme.colors.gray800,
    style: TextStyle = GureumTypography.displayMedium,
    maxLine: Int = 4,
) {
    Text(
        text = text,
        color = color,
        style = style,
        maxLines = maxLine,
        modifier = modifier,
        textAlign = TextAlign.Justify
    )
}

@Composable
fun Bold20Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GureumTheme.colors.gray800,
    style: TextStyle = GureumTypography.displaySmall,
    maxLine: Int = 4,
) {
    Text(
        text = text,
        color = color,
        style = style,
        maxLines = maxLine,
        modifier = modifier,
        textAlign = TextAlign.Justify
    )
}

@Composable
fun Semi24Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GureumTheme.colors.gray800,
    style: TextStyle = GureumTypography.headlineLarge,
    maxLine: Int = 4,
    textAlign: TextAlign = TextAlign.Justify
) {
    Text(
        text = text,
        color = color,
        style = style,
        maxLines = maxLine,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
fun Semi20Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GureumTheme.colors.gray800,
    style: TextStyle = GureumTypography.headlineMedium,
    maxLine: Int = 4,
) {
    Text(
        text = text,
        color = color,
        style = style,
        maxLines = maxLine,
        modifier = modifier,
        textAlign = TextAlign.Justify
    )
}

@Composable
fun Semi18Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GureumTheme.colors.gray800,
    style: TextStyle = GureumTypography.headlineSmall,
    maxLine: Int = 4,
) {
    Text(
        text = text,
        color = color,
        style = style,
        maxLines = maxLine,
        modifier = modifier,
        textAlign = TextAlign.Justify
    )
}

@Composable
fun Semi14Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GureumTheme.colors.gray700,
    style: TextStyle = GureumTypography.titleMedium,
    maxLine: Int = 4,
) {
    Text(
        text = text,
        color = color,
        style = style,
        maxLines = maxLine,
        modifier = modifier,
        textAlign = TextAlign.Justify
    )
}

@Composable
fun Semi12Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GureumTheme.colors.gray700,
    style: TextStyle = GureumTypography.titleSmall,
    maxLine: Int = 4,
) {
    Text(
        text = text,
        color = color,
        style = style,
        maxLines = maxLine,
        modifier = modifier,
        textAlign = TextAlign.Justify
    )
}

@Composable
fun Medi16Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GureumTheme.colors.gray700,
    style: TextStyle = GureumTypography.bodyLarge,
    maxLine: Int = 4,
    textAlign: TextAlign = TextAlign.Justify
) {
    Text(
        text = text,
        color = color,
        style = style,
        maxLines = maxLine,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
fun Medi14Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GureumTheme.colors.gray700,
    style: TextStyle = GureumTypography.bodyMedium,
    maxLine: Int = 4,
    textAlign: TextAlign = TextAlign.Justify
) {
    Text(
        text = text,
        color = color,
        style = style,
        maxLines = maxLine,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
fun Medi12Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GureumTheme.colors.gray700,
    style: TextStyle = GureumTypography.bodySmall,
    maxLine: Int = 4,
    textAlign: TextAlign = TextAlign.Justify
) {
    Text(
        text = text,
        color = color,
        style = style,
        maxLines = maxLine,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
fun Medi10Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GureumTheme.colors.gray700,
    style: TextStyle = GureumTypography.labelSmall,
    maxLine: Int = 4,
    textAlign: TextAlign = TextAlign.Justify
) {
    Text(
        text = text,
        color = color,
        style = style,
        maxLines = maxLine,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
fun BodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GureumTheme.colors.gray700,
    style: TextStyle = GureumTypography.bodyMedium,
    maxLine: Int = 4,
) {
    Text(
        text = text,
        color = color,
        style = style,
        maxLines = maxLine,
        modifier = modifier,
        textAlign = TextAlign.Justify
    )
}

@Composable
fun BodyMediumText(
    text: String,
    modifier: Modifier = Modifier.padding(),
    color: Color = GureumTheme.colors.gray500,
    style: TextStyle = GureumTypography.bodyMedium,
    maxLine: Int = 4,
) {
    Text(
        text = text,
        color = color,
        style = style,
        maxLines = maxLine,
        modifier = modifier,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun BodySubText(
    text: String,
    modifier: Modifier = Modifier.padding(top = 2.dp),
    color: Color = GureumTheme.colors.gray400,
    style: TextStyle = GureumTypography.bodySmall,
    maxLine: Int = 4,
) {
    Text(
        text = text,
        color = color,
        style = style,
        maxLines = maxLine,
        modifier = modifier,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    minLines: Int = 4,
    textStyle: TextStyle = GureumTypography.bodyMedium.copy(color = GureumTheme.colors.gray700),
    moreLabel: String = "더보기",
) {
    val moreStyle = SpanStyle(
        color = GureumTheme.colors.gray600,
        textDecoration = TextDecoration.Underline
    )
    var isExpanded by remember { mutableStateOf(false) }
    var annotatedText by remember(isExpanded) { mutableStateOf(AnnotatedString(text)) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() }, // 클릭 상태 추적
                indication = null // 리플 효과 제거
            ) { isExpanded = !isExpanded }
    ) {
        BasicText(
            text = annotatedText,
            modifier = Modifier.fillMaxWidth(),
            style = textStyle.copy(textAlign = TextAlign.Justify),
            maxLines = if (isExpanded) Int.MAX_VALUE else minLines,
            onTextLayout = { result -> // 레이아웃 결과 (오버플로우 감지)
                // 오버플로우 + 아직 확장되지 않았을 때
                if (result.hasVisualOverflow && !isExpanded) {
                    val lastLineIndex = result.lineCount - 1
                    val endOffset = result.getLineEnd(lastLineIndex) // 마지막 줄 끝 문자열 인덱스
                    val endIndex = (endOffset - (moreLabel.length + 2)).coerceAtLeast(0) // 더보기 글자만큼 자릿수 확보
                    val visibleText = text.substring(0, endIndex).trimEnd() // 펼쳐지지 않았을 때 보이는 문자열

                    annotatedText = buildAnnotatedString {
                        append(visibleText)
                        append("... ")
                        withStyle(moreStyle) { append(moreLabel) }
                    }
                }
            }
        )
    }
}

@Preview(name = "DarkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LightMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun GureumTitleTextPreview() {
    GureumPageTheme {
        Column {
            Semi16Text("필사한 문장", isUnderline = true)
            Semi16Text("필사한 문장")
        }
    }
}

@Preview(name = "DarkMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LightMode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun GureumTextPreview() {
    GureumPageTheme {
        Column {
            BodyText("네가 4시에 온다면 난 3시부터 행복할거야")
            Spacer(modifier = Modifier.height(10.dp))
            ExpandableText("네가 4시에 온다면 난 3시부터 행복할거야 네가 4시에 온다면 난 3시부터 행복할거야네가 4시에 온다면 난 3시부터 행복할거야 네가 4시에 온다면 난 3시부터 행복할거야 네가 4시에 온다면 난 3시부터 행복할거야네가 4시에 온다면 난 3시부터 행복할거야 네가 4시에 온다면 난 3시부터 행복할거야 네가 4시에 온다면 난 3시부터 행복할거야네가 4시에 온다면 난 3시부터 행복할거야")
        }
    }
}
