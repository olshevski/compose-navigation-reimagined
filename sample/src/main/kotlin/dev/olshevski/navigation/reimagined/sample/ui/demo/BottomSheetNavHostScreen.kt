package dev.olshevski.navigation.reimagined.sample.ui.demo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.olshevski.navigation.reimagined.material.BottomSheetNavHost
import dev.olshevski.navigation.reimagined.material.BottomSheetNavHostScope
import dev.olshevski.navigation.reimagined.material.BottomSheetValue
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.popAll
import dev.olshevski.navigation.reimagined.rememberNavController
import dev.olshevski.navigation.reimagined.sample.R
import dev.olshevski.navigation.reimagined.sample.singleLine
import dev.olshevski.navigation.reimagined.sample.ui.BottomSheetLayout
import dev.olshevski.navigation.reimagined.sample.ui.CenteredText
import dev.olshevski.navigation.reimagined.sample.ui.ContentLayout
import dev.olshevski.navigation.reimagined.sample.ui.ScreenLayout
import kotlinx.coroutines.launch

private enum class BottomSheetDestination {
    First,
    Second,
}

@Composable
fun BottomSheetNavHostScreen() = Box {
    val navController = rememberNavController<BottomSheetDestination>(
        initialBackstack = emptyList()
    )

    BackHandler(navController.backstack.entries.isNotEmpty()) {
        navController.pop()
    }

    ScreenLayout(
        title = stringResource(R.string.bottom_sheet_nav_host__demo_screen_title)
    ) {
        ContentLayout {
            CenteredText(
                text = "Use BottomSheetNavHost to open and switch between bottom sheets",
            )

            Button(
                onClick = {
                    navController.navigate(BottomSheetDestination.First)
                }
            ) {
                Text(stringResource(R.string.bottom_sheet_nav_host__open_first_sheet_button))
            }
        }
    }

    BottomSheetNavHost(
        backstack = navController.backstack,
        onDismissRequest = { navController.pop() }
    ) { destination ->
        when (destination) {
            BottomSheetDestination.First -> FirstBottomSheet(
                onOpenSecondSheetClick = {
                    navController.navigate(BottomSheetDestination.Second)
                }
            )
            BottomSheetDestination.Second -> SecondBottomSheet(
                onCloseClick = {
                    navController.pop()
                },
                onCloseAllClick = {
                    navController.popAll()
                }
            )
        }
    }
}

@Composable
private fun FirstBottomSheet(
    onOpenSecondSheetClick: () -> Unit
) = BottomSheetLayout(
    title = stringResource(R.string.bottom_sheet_nav_host__first_sheet_title),
) {
    CenteredText(
        text = "BottomSheetNavHost provides all the functionality of a regular NavHost"
    )

    Button(
        onClick = onOpenSecondSheetClick
    ) {
        Text(stringResource(R.string.bottom_sheet_nav_host__open_second_sheet_button))
    }
}

@Composable
private fun BottomSheetNavHostScope<BottomSheetDestination>.SecondBottomSheet(
    onCloseClick: () -> Unit,
    onCloseAllClick: () -> Unit
) = BottomSheetLayout(
    title = stringResource(R.string.bottom_sheet_nav_host__second_sheet_title),
    modifier = Modifier.verticalScroll(rememberScrollState())
) {
    val scope = rememberCoroutineScope()
    val isExpanded = this@SecondBottomSheet.sheetState.currentValue == BottomSheetValue.Expanded

    CenteredText(
        text = """You can swipe this bottom sheet to close it or fully expand. Alternatively, you 
            can control its state programmatically.""".singleLine()
    )

    Button(
        onClick = {
            scope.launch {
                if (isExpanded) {
                    this@SecondBottomSheet.sheetState.halfExpand()
                } else {
                    this@SecondBottomSheet.sheetState.expand()
                }
            }
        }
    ) {
        if (isExpanded) {
            Text(stringResource(R.string.bottom_sheet_nav_host__half_expand_sheet_button))
        } else {
            Text(stringResource(R.string.bottom_sheet_nav_host__expand_sheet_button))
        }
    }

    CenteredText(
        text = "To close a bottom sheet you need to remove its entry from NavController"
    )

    Button(
        onClick = onCloseClick
    ) {
        Text(stringResource(R.string.bottom_sheet_nav_host__close_sheet_button))
    }

    CenteredText(
        text = "Or even remove all entries"
    )

    Button(
        onClick = onCloseAllClick
    ) {
        Text(stringResource(R.string.bottom_sheet_nav_host__close_all_sheets_button))
    }

    CenteredText(
        text = "And here is a long text for testing nested scrolling:"
    )

    CenteredText(
        text = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Dignissim sodales ut eu sem integer vitae justo eget. Lectus magna fringilla urna porttitor. Vestibulum lorem sed risus ultricies tristique. A arcu cursus vitae congue mauris. Feugiat vivamus at augue eget arcu dictum varius duis at. Odio eu feugiat pretium nibh ipsum consequat nisl vel. A scelerisque purus semper eget duis at tellus at. Nisi porta lorem mollis aliquam ut porttitor leo a diam. Mauris nunc congue nisi vitae suscipit tellus mauris a. Tristique risus nec feugiat in fermentum posuere urna nec. Tempus egestas sed sed risus pretium quam vulputate dignissim. Commodo ullamcorper a lacus vestibulum sed arcu. Nisl rhoncus mattis rhoncus urna neque. Nunc non blandit massa enim nec dui.

            In massa tempor nec feugiat nisl. Placerat orci nulla pellentesque dignissim. Proin nibh nisl condimentum id venenatis a condimentum. Nec ultrices dui sapien eget mi proin sed libero. Ut placerat orci nulla pellentesque dignissim enim. Pellentesque diam volutpat commodo sed. Lacus sed viverra tellus in hac. Pellentesque elit ullamcorper dignissim cras tincidunt lobortis feugiat vivamus. Netus et malesuada fames ac turpis egestas integer eget. Sem et tortor consequat id porta nibh. Mattis vulputate enim nulla aliquet. Semper viverra nam libero justo. Tincidunt vitae semper quis lectus nulla. Vulputate sapien nec sagittis aliquam malesuada bibendum arcu vitae elementum. Viverra justo nec ultrices dui. Nec sagittis aliquam malesuada bibendum arcu. Auctor neque vitae tempus quam pellentesque nec. Urna id volutpat lacus laoreet non curabitur. Turpis egestas pretium aenean pharetra magna ac placerat vestibulum. Duis at tellus at urna condimentum mattis pellentesque id nibh.

            Non quam lacus suspendisse faucibus interdum posuere lorem. Maecenas accumsan lacus vel facilisis volutpat est velit. Commodo sed egestas egestas fringilla phasellus faucibus scelerisque eleifend. Gravida quis blandit turpis cursus in hac. Nec ullamcorper sit amet risus. Id neque aliquam vestibulum morbi blandit cursus. In hac habitasse platea dictumst quisque. Ac ut consequat semper viverra nam libero justo laoreet sit. Praesent tristique magna sit amet. Amet cursus sit amet dictum sit amet justo. Nulla facilisi cras fermentum odio eu feugiat pretium nibh ipsum. Eget nunc lobortis mattis aliquam faucibus purus. Id consectetur purus ut faucibus pulvinar. Rutrum tellus pellentesque eu tincidunt tortor aliquam nulla. Vel turpis nunc eget lorem dolor sed viverra ipsum nunc. Ut sem nulla pharetra diam sit amet nisl suscipit adipiscing.

            Etiam tempor orci eu lobortis elementum nibh tellus molestie. Mollis nunc sed id semper risus. Id leo in vitae turpis massa. Dui nunc mattis enim ut tellus elementum sagittis. Libero nunc consequat interdum varius sit amet mattis. Tempor commodo ullamcorper a lacus vestibulum sed arcu non. Neque egestas congue quisque egestas diam in arcu cursus. Lorem ipsum dolor sit amet consectetur. Velit ut tortor pretium viverra suspendisse potenti nullam. Condimentum vitae sapien pellentesque habitant. Nec tincidunt praesent semper feugiat nibh sed. Ac tincidunt vitae semper quis lectus nulla. Id venenatis a condimentum vitae. Dignissim diam quis enim lobortis scelerisque fermentum dui faucibus in. Urna et pharetra pharetra massa massa ultricies mi quis hendrerit. Turpis egestas integer eget aliquet nibh praesent tristique magna sit.

            Dui sapien eget mi proin sed libero enim. Vel turpis nunc eget lorem dolor sed. Odio pellentesque diam volutpat commodo sed egestas. Sit amet mattis vulputate enim nulla aliquet. Velit egestas dui id ornare arcu odio. Eget magna fermentum iaculis eu non diam. Ut enim blandit volutpat maecenas volutpat blandit aliquam etiam. Commodo ullamcorper a lacus vestibulum sed arcu non odio euismod. Venenatis urna cursus eget nunc scelerisque viverra mauris. Eget aliquet nibh praesent tristique magna. Turpis egestas sed tempus urna et pharetra. Massa ultricies mi quis hendrerit dolor.
        """.trimIndent()
    )
}