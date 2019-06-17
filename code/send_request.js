
// posts AJAX request to Ingress API.
// action: last part of the actual URL, the rpc/dashboard. is
//         added automatically
// data: JSON data to post. method will be derived automatically from
//       action, but may be overridden. Expects to be given Hash.
//       Strings are not supported.
// success: method to call on success. See jQuery API docs for avail-
//          able arguments: http://api.jquery.com/jQuery.ajax/
// error: see above. Additionally it is logged if the request failed.
window.postAjax = function(action, data, successCallback, errorCallback) {
  // state management functions... perhaps should be outside of this func?

//  var remove = function(data, textStatus, jqXHR) { window.requests.remove(jqXHR); };
//  var errCnt = function(jqXHR) { window.failedRequestCount++; window.requests.remove(jqXHR); };

  if (window.latestFailedRequestTime && window.latestFailedRequestTime < Date.now()-120*1000) {
    // no errors in the last two minutes - clear the error count
    window.failedRequestCount = 0;
    window.latestFailedRequestTime = undefined;
  }

  var onError = function(jqXHR, textStatus, errorThrown) {
    window.requests.remove(jqXHR);
    window.failedRequestCount++;

    window.latestFailedRequestTime = Date.now();

    // pass through to the user error func, if one exists
    if (errorCallback) {
      errorCallback(jqXHR, textStatus, errorThrown);
    }
  };

  var onSuccess = function(data, textStatus, jqXHR) {
    window.requests.remove(jqXHR);

    // the Niantic server can return a HTTP success, but the JSON response contains an error. handle that sensibly
    if (data && data.error && data.error == 'out of date') {
      window.failedRequestCount++;
      // let's call the error callback in thos case...
      if (errorCallback) {
        errorCallback(jqXHR, textStatus, "data.error == 'out of date'");
      }

      window.outOfDateUserPrompt();
    } else {
      successCallback(data, textStatus, jqXHR);
    }
  };

  // we set this flag when we want to block all requests due to having an out of date CURRENT_VERSION
  if (window.blockOutOfDateRequests) {
    window.failedRequestCount++;
    window.latestFailedRequestTime = Date.now();

    // call the error callback, if one exists
    if (errorCallback) {
      // NOTE: error called on a setTimeout - as it won't be expected to be synchronous
      // ensures no recursion issues if the error handler immediately resends the request
      setTimeout(function(){errorCallback(null, undefined, "window.blockOutOfDateRequests is set");}, 10);
    }
    return;
  }

  var versionStr = niantic_params.CURRENT_VERSION;
  var post_data = JSON.stringify($.extend({}, data, {v: versionStr}));

  var result = $.ajax({
    url: '/r/'+action,
    type: 'POST',
    data: post_data,
    context: data,
    dataType: 'json',
    success: [onSuccess],
    error: [onError],
    contentType: 'application/json; charset=utf-8',
    beforeSend: function(req) {
      req.setRequestHeader('X-CSRFToken', readCookie('csrftoken'));
    }
  });
  result.action = action;

  requests.add(result);

  return result;
}


window.outOfDateUserPrompt = function()
{
  // we block all requests while the dialog is open. 
  if (!window.blockOutOfDateRequests) {
    window.blockOutOfDateRequests = true;

    dialog({
      title: '刷新 IITC',
      html: '<p>IITC正在使用过时的程序。这可能在Niantic更新intel网站时发生。</p>'
           +'<p>你需要重新加载网页以取得更新。/p>'
           +'<p>如果您已经刷新过网页，那可能是因为intel的脚本仍被缓存在某个地方。'
           +'这种情况下，尝试清理你的缓存，或等待15-30分钟，直到旧的缓存过期。</p>',
      buttons: {
        '刷新': function() {
          if (typeof android !== 'undefined' && android && android.reloadIITC) {
            android.reloadIITC();
          } else {
            window.location.reload();
          }
        }
      },
      close: function(event, ui) {
        delete window.blockOutOfDateRequests;
      }

    });


  }

}
