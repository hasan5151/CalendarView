package kg.beeline.shared.extensions

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.IdRes
import androidx.core.view.get
import androidx.core.view.iterator
import androidx.navigation.NavController
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import kg.beeline.shared.R
import kg.beeline.shared.extensions.BottomNavigationKtx.NavAnimDirection.*
import java.lang.ref.WeakReference
import androidx.navigation.ui.R as RX

/**
 * Created by Jahongir on 28/11/2020
 */
fun BottomNavigationView.setupNavigation(navController: NavController, alwaysPopUpHome: Boolean = true) {
    BottomNavigationKtx.setupNavController(this, navController, alwaysPopUpHome)
}

object BottomNavigationKtx {

    /**
     * Sets up a {@link BottomNavigationView} for use with a {@link NavController}. This will call
     * {@link #onNavDestinationSelected(MenuItem, NavController)} when a menu item is selected. The
     * selected item in the BottomNavigationView will automatically be updated when the destination
     * changes.
     *
     * @param bottomNavigationView The BottomNavigationView that should be kept in sync with
     *                             changes to the NavController.
     * @param navController The NavController that supplies the primary menu.
     *                      Navigation actions on this NavController will be reflected in the
     *                      selected item in the BottomNavigationView.
     */
    fun setupNavController(bottomNavigationView: BottomNavigationView, navController: NavController, popUpHome: Boolean) {
        val weakReference = WeakReference(bottomNavigationView)

        /**Set mock listener to prevent re selecting items*/
        bottomNavigationView.setOnNavigationItemReselectedListener { }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val view = weakReference.get() ?: return@setOnNavigationItemSelectedListener false
            onNavDestinationSelected(view, item, navController, popUpHome)
        }
        bottomNavigationView.selectedItemId


        navController.addOnDestinationChangedListener(object : OnDestinationChangedListener {
            override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
                val view = weakReference.get()
                if (view == null) {
                    navController.removeOnDestinationChangedListener(this)
                    return
                }
                val menu = view.menu
                for (i in 0 until menu.size()) {
                    val item = menu.getItem(i)
                    if (destination.id == item.itemId) {
                        item.isChecked = true
                        break
                    }
                }
            }
        })
    }

    /**
     * Attempt to navigate to the {@link NavDestination} associated with the given MenuItem. This
     * MenuItem should have been added via one of the helper methods in this class.
     *
     * <p>Importantly, it assumes the {@link MenuItem#getItemId() menu item id} matches a valid
     * {@link NavDestination#getAction(int) action id} or
     * {@link NavDestination#getId() destination id} to be navigated to.</p>
     * <p>
     * By default, the back stack will be popped back to the navigation graph's start destination.
     * Menu items that have <code>android:menuCategory="secondary"</code> will not pop the back
     * stack.
     *
     * @param item The selected MenuItem.
     * @param navController The NavController that hosts the destination.
     * @return True if the {@link NavController} was able to navigate to the destination
     * associated with the given MenuItem.
     */


    private fun onNavDestinationSelected(bottomNavigationView: BottomNavigationView, item: MenuItem,
                                         navController: NavController, isPopUpHome: Boolean): Boolean {

        val navMenu = bottomNavigationView.menu

        val currentItemId = bottomNavigationView.selectedItemId
        val selectedItemId = item.itemId

        val currentOrder = findMenuOrder(currentItemId, navMenu)
        val selectedOrder = findMenuOrder(selectedItemId, navMenu)

        /** Prevent from reselect current destination*/
        if (currentItemId == selectedItemId) return false

        val builder: NavOptions.Builder

        /** If menu order sets it will slide depends on order*/
        builder = if (currentOrder != null && selectedOrder != null && currentOrder != selectedOrder) {
            val direction = if (selectedOrder > currentOrder) RIGHT else LEFT
            getNavOptionsBuilder(direction)
        } else {
            NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setEnterAnim(RX.animator.nav_default_enter_anim)
                    .setExitAnim(RX.animator.nav_default_exit_anim)
                    .setPopEnterAnim(RX.animator.nav_default_pop_enter_anim)
                    .setPopExitAnim(RX.animator.nav_default_pop_exit_anim)
        }


        if (isPopUpHome) {
            builder.setPopUpTo(navMenu[0].itemId, false)
        }

        val options = builder.build()
        return try {
            //TODO provide proper API instead of using Exceptions as Control-Flow.
            navController.navigate(item.itemId, null, options)
            true
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            false
        }
    }


    /**
     * Determines whether the given `destId` matches the NavDestination. This handles
     * both the default case (the destination's id matches the given id) and the nested case where
     * the given id is a parent/grandparent/etc of the destination.
     */
    private fun matchDestination(destination: NavDestination, @IdRes destId: Int): Boolean {
        var currentDestination: NavDestination? = destination
        while (currentDestination?.id != destId && currentDestination?.parent != null) {
            currentDestination = currentDestination.parent
        }
        return currentDestination?.id == destId
    }

    private fun findMenuOrder(itemId: Int, menu: Menu): Int? {
        for (item in menu) {
            if (item.itemId == itemId) {
                return item.order
            }
        }
        return null
    }

    private fun getNavOptionsBuilder(direction: NavAnimDirection): NavOptions.Builder {
        @AnimRes @AnimatorRes
        val enterAnim: Int

        @AnimRes @AnimatorRes
        val exitAnim: Int

        @AnimRes @AnimatorRes
        val popUpEnterAnim: Int

        @AnimRes @AnimatorRes
        val popUpExitAnim: Int
        when (direction) {
            RIGHT -> {
                enterAnim = R.anim.slide_in_right
                exitAnim = R.anim.slide_out_left
                popUpEnterAnim = R.anim.slide_in_left
                popUpExitAnim = R.anim.slide_out_right
            }
            LEFT -> {
                enterAnim = R.anim.slide_in_left
                exitAnim = R.anim.slide_out_right
                popUpEnterAnim = R.anim.slide_in_left
                popUpExitAnim = R.anim.slide_out_right
            }
        }
        return NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(enterAnim)
                .setExitAnim(exitAnim)
                .setPopEnterAnim(popUpEnterAnim)
                .setPopExitAnim(popUpExitAnim)
    }

    private enum class NavAnimDirection { RIGHT, LEFT }

    /**
     * Finds the actual start destination of the graph, handling cases where the graph's starting
     * destination is itself a NavGraph.
     */
    private fun findStartDestination(graph: NavGraph): NavDestination? {
        var startDestination: NavDestination? = graph
        while (startDestination is NavGraph) {
            val parent = startDestination
            startDestination = parent.findNode(parent.startDestinationId)
        }
        return startDestination
    }


    private fun findMenuHomeDestination(menu: Menu, graph: NavGraph): NavDestination? {
        val startItem = menu.getItem(0)
        var homeDestination: NavDestination? = null
        for (destination in graph.iterator()) {
            if (destination.id == startItem.itemId) {
                homeDestination = destination
                break
            }
        }
        return homeDestination
    }

}