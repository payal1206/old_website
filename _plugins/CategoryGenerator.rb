module Jekyll
  class CategoryAndTagSpecificPagination < Generator
    safe true

    def generate(site)
      site.categories.each do |category|
        build(site, "category", category)
      end

      site.tags.each do |tag|
        build(site, "tag", tag)
      end
    end

    def build(site, type, posts)
      posts[1] = posts[1].sort_by{|p|-p.date.to_f}
      paginate(site, type, posts)
    end

    def paginate(site, type, posts)
      pages = Pager.calculate_pages(posts[1], site.config["paginate"].to_i)
      (1..pages).each do |number|
        pager = Pager.new(site.config, number, posts[1], pages)
        path = "/#{type}/#{posts[0]}"
        if number > 1
          path = path + "/page#{number}"
        end
        newpage = GroupSubPage.new(site, site.source, path, type, posts[0])
        newpage.pager = pager
        site.pages << newpage
      end
    end
  end

  class GroupSubPage < Page
    def initialize(site, base, dir, type, val)
      @site = site
      @base = base
      @dir  = dir
      @name = "index.html"

      self.process(@name)
      self.read_yaml(File.join(base, "_layouts"), "sublist.html")
      self.data["grouptype"] = type
      self.data[type] = val

      @type = type == "tag" ? "tags" : "categories"
      #if type == "tag"
        if site.config[@type]
          site.config[@type].each do |entry|
            if entry["name"] == val
              if entry["title"]
                self.data["type_title"] = entry["title"]
              end
              if entry["description"]
                self.data["type_description"] = entry["description"]
              end
              break
            end
          end
        end
      #end
    end
  end
end
