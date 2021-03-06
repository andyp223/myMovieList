<#assign content>
<div class="flex-container content-align">
  <#if movie.title??>
    <div class="flex-row">
      <span id="movie-id" class="${movie.getImdbID()}"></span>
      <#if movie.getImg()??>
        <div class="flex-col">
          <img id="movie-page-img" class="movie-img ${movie.getImdbID()} colored-border-img" src="${movie.getImg()}"/>
        </div>
      </#if>
      <!-- Movie information -->
      <div class="flex-col">
        <div class="flex-container">
          <div class="flex-row">
            <div class="flex-col">
              <h2 class="contrast-color">Film</h2>
              <h3>${movie.title}</h3>
            </div>
            <div class="flex-col">
              <h2 class="contrast-color">Rating</h2>
              <h3>${movie.imdbRating} / 10</h3>
            </div>
          </div>
          <div class="flex-row">
            <div class="flex-col">
              <h2 class="contrast-color">Released</h2>
              <h3>${movie.year?c}</h3>
            </div>
            <div class="flex-col">
              <h2 class="contrast-color">Rated</h2>
              <h3>${movie.rated}</h3>
            </div>
          </div>
        </div>
        <p><strong>Plot: </strong> ${movie.plot}</p>
        <p><strong>Awards: </strong> ${movie.awards}</p>
        <p><strong>Genres: </strong>
          <#list movie.genre as genre>
            ${genre}
          </#list>
        </p>
      </div>
    </div>
  <#else>
    <h3>Invalid movie.</h3>
  </#if>
</div>
<#if !username.username??>
  <div class="flex-container content-align">
    <div class="row">
      <div class="flex-col">
        <form class= "formlst">
          <div class="form-group">
            <select class="form-control" name="${movie.getImdbID()}">
              <#list userLists as lst>
                <option value=${lst.getLeft()}>${lst.getRight()}</option>
              </#list>
            </select>
          </div>
          <button type="submit" class = "btn btn-dark" id = "add">Add To List</button>
        </form>
      </div>
      <div class="flex-col">
        <!-- WATCH LATER BUTTON HERE -->
        <form class= "formWatchLater">
          <button type="submit" class = "btn btn-dark" id = "watchLater">Watch Later</button>
        </form>
      </div>
      <div class="flex-col">
        <form id= "ratingForm">
          <input type="number" id="rating1" min="1" max="10">
          <button type="submit" class = "btn btn-dark" id = "rating">Add Rating</button>
        </form>
      </div>
    </div>
  </div>
</#if>
</#assign>
<#include "main.ftl">
