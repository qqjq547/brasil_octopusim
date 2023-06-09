package framework.telegram.message.ui.preview

import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import framework.telegram.support.BaseFragment


/**
 * Created by lzh on 19-8-30.
 * INFO:
 */
abstract class LazyFragment : BaseFragment(){

    /**
     * rootView是否初始化标志，防止回调函数在rootView为空的时候触发
     */
    private var hasCreateView: Boolean = false

    /**
     * 当前Fragment是否处于可见状态标志，防止因ViewPager的缓存机制而导致回调函数的触发
     */
    protected var isFragmentVisible: Boolean = false

    private var hasLoad = false
    /**
     * onCreateView()里返回的view，修饰为protected,所以子类继承该类时，在onCreateView里必须对该变量进行初始化
     */
    protected var rootView: View? = null

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (rootView == null) {
            return
        }
        hasCreateView = true
        if (isVisibleToUser) {
            onFragmentVisibleChange(true)
            isFragmentVisible = true
            return
        }
        if (isFragmentVisible) {
            onFragmentVisibleChange(false)
            isFragmentVisible = false
        }
    }

    override fun onCreate( savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVariable()
    }

    override  fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!hasCreateView && userVisibleHint) {
            onFragmentVisibleChange(true)
            isFragmentVisible = true
        }
    }

    private fun initVariable() {
        hasCreateView = false
        isFragmentVisible = false
    }

    /**************************************************************
     * 自定义的回调方法，子类可根据需求重写
     */

    /**
     * 当前fragment可见状态发生变化时会回调该方法
     * 如果当前fragment是第一次加载，等待onCreateView后才会回调该方法，其它情况回调时机跟 [.setUserVisibleHint]一致
     * 在该回调方法中你可以做一些加载数据操作，甚至是控件的操作，因为配合fragment的view复用机制，你不用担心在对控件操作中会报 null 异常
     *
     * @param isVisible true  不可见 -> 可见
     * false 可见  -> 不可见
     */
    open protected fun onFragmentVisibleChange(isVisible: Boolean){
        if (isVisible && !hasLoad){
            hasLoad = true
            lazyLoad()
        }
    }

    open protected fun lazyLoad(){

    }

    open  fun pauseFragment(){

    }
}