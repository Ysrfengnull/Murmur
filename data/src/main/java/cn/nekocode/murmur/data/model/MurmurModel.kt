package cn.nekocode.murmur.data.model

import cn.nekocode.murmur.data.DataLayer
import cn.nekocode.murmur.data.dto.Murmur
import cn.nekocode.murmur.data.service.LeancloudService
import com.orhanobut.hawk.Hawk
import rx.Notification
import rx.Observable
import rx.schedulers.Schedulers

/**
 * Created by nekocode on 3/13/16.
 */
object MurmurModel {

    fun getMurmurs(): Observable<List<Murmur>> =
            LeancloudService.api.getMurmurs(50, "-updatedAt")
                    .subscribeOn(Schedulers.io())
                    .map {
                        val murmurs = it.results
                        murmurs.map {
                            it.file.url = DataLayer.mediaProxy.getProxyUrl(it.file.url)
                            it
                        }
                    }
                    .doOnEach {
                        if(it.kind == Notification.Kind.OnNext) {
                            Hawk.put("murmurs", it.value)
                        }
                    }
                    .onErrorResumeNext {
                        val list: List<Murmur>? = Hawk.get("murmurs")
                        Observable.just(list)
                    }

}