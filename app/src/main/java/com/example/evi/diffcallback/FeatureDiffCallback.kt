package com.example.evi.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.example.evi.repository.Features

class FeatureDiffCallback(private val mOldList: List<Features>,
                          private val mNewList: List<Features>): DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldList[oldItemPosition].attributes!!.FID == mNewList[newItemPosition].attributes!!.FID
    }

    override fun getOldListSize() = mOldList.size


    override fun getNewListSize() = mNewList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldList[oldItemPosition] == mNewList[newItemPosition]
    }
}