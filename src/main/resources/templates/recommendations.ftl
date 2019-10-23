<h5 class="text-muted">Products you might like</h5>
<div class="row my-2 py-3">
<#list recommend as rec>
        <div class="col-sm-6 col-md-4 col-lg-3">
            <div class="card mb-3 box-shadow">
                <a href="/product/${rec.id}">
                    <img class="card-img-top border-bottom" alt =""
                        style="width: 100%; height: auto;"
                        src="${rec.picture}">
                </a>
                <div class="card-body text-center py-2">
                    <small class="card-title text-muted">
                        ${rec.name}
                    </small>
                </div>
            </div>
        </div>
        </#list>
</div>