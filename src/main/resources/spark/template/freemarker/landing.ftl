<#assign content>
<!-- Each of these flex containers should contain a row of movie poster images,
as well as a clickable link to the respective movie link-->
<div class="container" id="top-movie-container">
  <!-- change this for category name -->
  <div class = "row">
    <h2>Top Rated</h2>
  </div>
  <div class="row">
    <#list moviesTop as movie>
      <div class="col">
        <!-- image of the movie -->
        <img class="movie-img ${movie.getImdbID()}" src="${movie.getImg()}"/>
      </div>
      <div class="movie-popup colored-border" id="${movie.getImdbID()}">
        <div class="flex-row">
          <span class="exit-member-info">
            <span class="x x-member"></span>
          </span>
          <div class="flex-col">
            <!-- blown up image of the movie -->
            <img src="${movie.getImg()}"/>
          </div>
          <div class="flex-col">
            <!-- movie information like title, rating -->
            <h3>Film: ${movie.title}</h3>
            <h4>Rating: ${movie.imdbRating}</h4>
            <br/>
            <p><a href="/m/${movie.getImdbID()}">More information on this movie...</a></p>
          </div>
        </div>
      </div>
    </#list>
  </div>

  <!-- change this for category name -->
  <div class = "row">
    <h2>Top Rated</h2>
  </div>
  <div class="row">
    <#list moviesTopMid as movie>
      <div class="col">
        <!-- image of the movie -->
        <img class="movie-img ${movie.getImdbID()}" src="${movie.getImg()}"/>
      </div>
      <div class="movie-popup colored-border" id="${movie.getImdbID()}">
        <div class="flex-row">
          <span class="exit-member-info">
            <span class="x x-member"></span>
          </span>
          <div class="flex-col">
            <!-- blown up image of the movie -->
            <img src="${movie.getImg()}"/>
          </div>
          <div class="flex-col">
            <!-- movie information like title, rating -->
            <h3>Film: ${movie.title}</h3>
            <h4>Rating: ${movie.imdbRating}</h4>
            <br/>
            <p><a href="/m/${movie.getImdbID()}">More information on this movie...</a></p>
          </div>
        </div>
      </div>
    </#list>
  </div>

  <!-- change this for category name -->
  <div class = "row">
    <h2>Top Rated</h2>
  </div>
  <div class="row">
    <#list moviesBotMid as movie>
      <div class="col">
        <!-- image of the movie -->
        <img class="movie-img ${movie.getImdbID()}" src="${movie.getImg()}"/>
      </div>
      <div class="movie-popup colored-border" id="${movie.getImdbID()}">
        <div class="flex-row">
          <span class="exit-member-info">
            <span class="x x-member"></span>
          </span>
          <div class="flex-col">
            <!-- blown up image of the movie -->
            <img src="${movie.getImg()}"/>
          </div>
          <div class="flex-col">
            <!-- movie information like title, rating -->
            <h3>Film: ${movie.title}</h3>
            <h4>Rating: ${movie.imdbRating}</h4>
            <br/>
            <p><a href="/m/${movie.getImdbID()}">More information on this movie...</a></p>
          </div>
        </div>
      </div>
    </#list>
  </div>

  <!-- change this for category name -->
  <div class = "row">
    <h2>Top Rated</h2>
  </div>
  <div class="row">
    <#list moviesBot as movie>
    <div class="col">
      <!-- image of the movie -->
      <img class="movie-img ${movie.getImdbID()}" src="${movie.getImg()}"/>
    </div>
      <div class="movie-popup colored-border" id="${movie.getImdbID()}">
        <div class="flex-row">
          <span class="exit-member-info">
            <span class="x x-member"></span>
          </span>
          <div class="flex-col">
            <!-- blown up image of the movie -->
            <img src="${movie.getImg()}"/>
          </div>
          <div class="flex-col">
            <!-- movie information like title, rating -->
            <h3>Film: ${movie.title}</h3>
            <h4>Rating: ${movie.imdbRating}</h4>
            <br/>
            <p><a href="/m/${movie.getImdbID()}">More information on this movie...</a></p>
          </div>
        </div>
      </div>
    </#list>
  </div>
</div>
<!-- TO GREY OUT REST OF THE MOBILE SITE -->
<div class="mobile-overlay"></div>
</#assign>
<#include "main.ftl">