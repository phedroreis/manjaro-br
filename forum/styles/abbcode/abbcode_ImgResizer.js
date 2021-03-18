function ArrayPush(arr,val){val=url_clean(val);var found=contains(val,arr);if(!found[0]){arr.push(val);}}
if(typeof Array.prototype.push===' undefined'){Array.prototype.push=function(value){this[this.length]=value;};}
function contains(v,a){for(var j=0;j<a.length;j++){if(a[j]===v){return new Array(true,j);}}
return new Array(false,0);}
function url_clean(url)
{url=decodeURI(url.toString().replace(/\s/g,' '));if(url.indexOf('download/')!==-1)
{var param1=url.substring(0,url.indexOf('?'));var param2=url.substring(url.indexOf('id='));var param3=param2.substring(param2.indexOf('id='),param2.indexOf('&'));url=param1+'?'+(param3?param3:param2);}
return url;}
function name_clean(ObjImage)
{var title='';if(ObjImage.className.indexOf('resize_me')!==-1)
{title=ObjImage.src.substring(ObjImage.src.lastIndexOf('/')+1);}
else if((ObjImage.className.indexOf('attach_me')!==-1)||(ObjImage.className.indexOf('attach_parent')!==-1))
{title=ObjImage.alt;}
else
{title=ObjImage.src;}
return title;}
function image_scale(obj,img_obj,img_src,new_src,img_width,img_height)
{var new_img=new Image();new_img.src=new_src;img_obj.src=new_src;if(obj!==img_obj){obj.style.width=new_img.width+'px';}
var Wait_pass=0;var timer=window.setInterval(function()
{Wait_pass++;if(Wait_pass>=120){window.clearInterval(timer);return false;}
if(new_img.readyState==='complete'||new_img.complete)
{window.clearInterval(timer);img_obj.width=new_img.width;img_obj.height=new_img.height;img_obj.onclick=function(){return image_unscale(obj,img_obj,img_src,new_src,img_width,img_height);};}},10);}
function image_unscale(obj,img_obj,img_src,new_src,img_width,img_height)
{img_obj.src=img_src;img_width=(img_width>0)?img_width:ImageResizerMaxWidth;img_height=(img_height>0)?img_height:ImageResizerMaxHeight;if(obj!==img_obj){obj.style.width=img_width+'px';}
img_obj.width=img_width;img_obj.height=img_height;img_obj.onclick=function(){return image_scale(obj,img_obj,img_src,new_src,img_width,img_height);};}
function wrap_by_anchor(ObjImage,objResizerDiv,mode)
{ObjImage.className=ObjImage.className+' resized';var anchor,clone,fragment,source;if(mode!=='attach_parent')
{clone=ObjImage.cloneNode(false);source=ObjImage;fragment=document.createDocumentFragment();anchor=document.createElement('a');anchor.href=url_clean(ObjImage.src);anchor.title=name_clean(ObjImage);}
switch(ImageResizerMode)
{case 'AdvancedBox':if(mode==='attach_parent')
{ArrayPush(AdvancedBox.SlideShows,ObjImage.parentNode.href);ObjImage.onclick=function(){return AdvancedBox.Start(this.parentNode.href);};}
else
{ArrayPush(AdvancedBox.SlideShows,ObjImage.src);ObjImage.onclick=function(){return AdvancedBox.Start(this.src);};}
return true;case 'HighslideBox':if(mode==='attach_parent')
{ObjImage.parentNode.className=ObjImage.parentNode.className+' highslide';ObjImage.parentNode.onclick=function(){return hs.expand(this,{slideshowGroup:'gallery'});};return true;}
else
{anchor.className=anchor.className+' highslide';anchor.onclick=function(){return hs.expand(anchor,{slideshowGroup:'gallery'});};}
break;case 'Lightview':if(mode==='attach_parent')
{ObjImage.parentNode.className=ObjImage.parentNode.className+' lightview';ObjImage.parentNode.rel='gallery[lightview_gallery]';return true;}
else
{anchor.className=(anchor.className)?anchor.className+' lightview':'lightview';anchor.rel='gallery[lightview_gallery]';}
break;case 'prettyPhoto':if(mode==='attach_parent')
{ObjImage.parentNode.rel='prettyPhoto[gallery]';return true;}
else
{anchor.rel='prettyPhoto[gallery]';}
break;case 'Shadowbox':if(mode==='attach_parent')
{ObjImage.parentNode.className=ObjImage.parentNode.className+' shadowbox-gallery';ObjImage.parentNode.setAttribute('rel','shadowbox;player=img');return true;}
else
{anchor.className=(anchor.className)?anchor.className+' shadowbox-gallery':'shadowbox-gallery';anchor.rel='shadowbox;player=img';}
break;case 'pop-up':if(mode==='attach_parent')
{ObjImage.onclick=function(){return popup(this.parentNode.href,this.width,this.height);};}
else
{var popup_url=(ObjImage.className.indexOf('attach_parent')!==-1)?ObjImage.parentNode.href:ObjImage.src;var popup_width=(ObjImage.width+30);var popup_height=(ObjImage.height+30);ObjImage.onclick=function(){return popup(popup_url,popup_width,popup_height,name_clean(ObjImage));};}
return true;case 'enlarge':if(mode==='attach_parent')
{ObjImage.onclick=function(){return image_scale(this,this,this.src,this.parentNode.href,this.width,this.height);};}
else
{if(objResizerDiv)
{ObjImage.onclick=function(){return image_scale(objResizerDiv,this,this.src,this.src,this.width,this.height);};}
else
{ObjImage.onclick=function(){return image_scale(this,this,this.src,this.src,this.width,this.height);};}}
return true;case 'samewindow':if(mode==='attach_parent')
{ObjImage.onclick=function(){return window.open(this.parentNode.href.replace(/&amp;/g,'&'),'_self','resizable=yes,scrollbars=yes');};}
else
{ObjImage.onclick=function(){return window.open(ObjImage.src,'_self');};}
return true;default:case 'newwindow':if(mode==='attach_parent')
{ObjImage.onclick=function(){return window.open(this.parentNode.href,'_blank');};}
else
{ObjImage.onclick=function(){return window.open(ObjImage.src,'_blank');};}
return true;}
anchor.appendChild(clone);fragment.appendChild(anchor);source.parentNode.replaceChild(fragment,source);return true;}
function ImageResizerOn(ObjImage)
{var ResizerId='image_'+Math.floor(Math.random()*(100));var ResizerW=ObjImage.width;var ResizerH=ObjImage.height;var ResizerP=0;var objResizerDiv;if(ObjImage.width>ImageResizerMaxWidth&&ImageResizerMaxWidth>0&&ObjImage.width>0)
{ObjImage.width=ImageResizerMaxWidth;ObjImage.height=(ImageResizerMaxWidth/ResizerW)*ResizerH;ResizerP=Math.ceil(parseInt(ObjImage.width/ResizerW*100,10));}
if(ObjImage.height>ImageResizerMaxHeight&&ImageResizerMaxHeight>0&&ObjImage.height>0)
{ObjImage.height=ImageResizerMaxHeight;ObjImage.width=(ImageResizerMaxHeight/ResizerH)*ResizerW;ResizerP=Math.ceil(parseInt(ObjImage.height/ResizerH*100,10));}
if(!ImageResizerUseBar)
{if(ObjImage.fileSize&&ObjImage.fileSize>0)
{ObjImage.title=ImageResizerWarningFilesize.replace('%1$s',ResizerW).replace('%2$s',ResizerH).replace('%3$s',Math.round(ObjImage.fileSize/1024))+"\n\r"+ImageResizerWarningSmall;}
else
{ObjImage.title=ImageResizerWarningNoFilesize.replace('%1$s',ResizerW).replace('%2$s',ResizerH)+"\n\r"+ImageResizerWarningSmall;}}
else
{objResizerDiv=document.createElement('div');objResizerDiv.className='resized-div';objResizerDiv.style.width=ObjImage.width+'px';if(ObjImage.parentNode.style.textAlign==='right')
{objResizerDiv.style.marginLeft='auto';}
if(ObjImage.fileSize&&ObjImage.fileSize>0)
{objResizerDiv.title=ImageResizerWarningFilesize.replace('%1$s',ResizerW).replace('%2$s',ResizerH).replace('%3$s',Math.round(ObjImage.fileSize/1024))+"\n\r"+ImageResizerWarningSmall;}
else
{objResizerDiv.title=ImageResizerWarningNoFilesize.replace('%1$s',ResizerW).replace('%2$s',ResizerH)+"\n\r"+ImageResizerWarningSmall;}
var objResizerSpan=document.createElement('span');objResizerSpan.className='resized-txt';var objResizerText=document.createTextNode('');if(ObjImage.width<=250)
{objResizerText.data=ImageResizerWarningSmall;}
else
{objResizerText.data=ImageResizerWarningFullsize.replace('%1$s',ResizerP).replace('%2$s',ResizerW).replace('%3$s',ResizerH);}
objResizerSpan.appendChild(objResizerText);objResizerDiv.appendChild(objResizerSpan);ObjImage.parentNode.insertBefore(objResizerDiv,ObjImage);}
wrap_by_anchor(ObjImage,objResizerDiv,null);}
function ImgOnLoad()
{var include_signatures=(ImageResizerSignature===1)?true:false;var include_thumbnail_abbc3=true;var include_thumbnail_attached=true;var include_images_attached=true;var sig_images_ary=[];var sig_elm_ary=[].slice.call(getElementsByClassName('signature','div')).concat([].slice.call(getElementsByClassName('postbody','span')));for(var e=0,sea=sig_elm_ary.length;e<sea;e++)
{if(sig_elm_ary[e].id)
{var sig_img_ary=getElementsByClassName('resize_me','img',document.getElementById(sig_elm_ary[e].id));for(var i=0,sia=sig_img_ary.length;i<sia;i++){sig_images_ary.push(sig_img_ary[i].src);}}}
var posted_images_ary=MyGetElementsByClassName('resize_me|attach_me|attach_parent');for(var pia=0;pia<posted_images_ary.length;pia++)
{var img=posted_images_ary[pia];ImageResizerMaxWidth=ImageResizerMaxWidth_post;ImageResizerMaxHeight=ImageResizerMaxHeight_post;if(!include_signatures&&sig_images_ary.length>0)
{if(contains(img.src,sig_images_ary)[0]){continue;}}
else if(include_signatures&&sig_images_ary.length>0)
{if(contains(img.src,sig_images_ary)[0])
{ImageResizerMaxWidth=ImageResizerMaxWidth_sig;ImageResizerMaxHeight=ImageResizerMaxHeight_sig;}}
if((img.className==='hoverbox resize_me'&&!include_thumbnail_abbc3)||(img.className==='attach_parent'&&!include_thumbnail_attached)||(img.className==='attach_me'&&!include_images_attached)||((img.className==='resize_me'||img.className==='attach_me')&&(!ImageResizerMaxWidth||ImageResizerMaxWidth>0&&img.width<=ImageResizerMaxWidth)&&(!ImageResizerMaxHeight||ImageResizerMaxHeight>0&&img.height<=ImageResizerMaxHeight)))
{continue;}
switch(img.className)
{case 'hoverbox resize_me':wrap_by_anchor(img,null,null);break;case 'resize_me':ImageResizerOn(img);break;case 'attach_me':ImageResizerOn(img);break;case 'attach_parent':img.parentNode.onclick=function(){return false;};img.parentNode.href=url_clean(img.parentNode.href);img.parentNode.title=name_clean(img);wrap_by_anchor(img,null,'attach_parent');break;default:break;}}
switch(ImageResizerMode)
{case 'prettyPhoto':$(function(){$("a[rel^='prettyPhoto']").prettyPhoto({opacity:0.60,show_title:false,deeplinking:false,social_tools:false,overlay_gallery:false,theme:'pp_default'});});break;case 'Shadowbox':Shadowbox.init({overlayOpacity:0.8});Shadowbox.setup('a.shadowbox-gallery',{gallery:'shadowbox-gallery',player:'img',continuous:true,counterType:'skip',handleOversize:'resize'});break;}
return true;}
if(window.onload_functions)
{onload_functions.push('ImgOnLoad()');}
else if(typeof(window.addEventListener)!=='undefined')
{window.addEventListener('load',ImgOnLoad,false);}
else if(typeof(window.attachEvent)!=='undefined')
{window.attachEvent('onload',ImgOnLoad);}